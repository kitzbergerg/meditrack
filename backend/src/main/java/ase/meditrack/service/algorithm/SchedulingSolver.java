package ase.meditrack.service.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
public final class SchedulingSolver {
    private static final int MAX_RUNTIME_IN_SECONDS = 10;

    static {
        Loader.loadNativeLibraries();
    }

    private SchedulingSolver() {
    }

    /**
     * @param input the information the algorithm uses
     * @return AlgorithmOutput if a valid assignment was found; empty otherwise
     */
    public static Optional<AlgorithmOutput> solve(final AlgorithmInput input) {
        final int minNumberOfDaysInMonth = 28;
        if (input.numberOfDays() < minNumberOfDaysInMonth || input.employees().isEmpty()
                || input.shiftTypes().isEmpty()) {
            throw new RuntimeException("invalid input");
        }
        for (ShiftTypeInfo shiftTypeInfo : input.shiftTypes()) {
            if (shiftTypeInfo.duration() <= 0 || shiftTypeInfo.duration() > 12) {
                throw new RuntimeException("invalid shiftTypeInfo duration");
            }
        }
        for (EmployeeInfo employeeInfo : input.employees()) {
            if (employeeInfo.optimalWorkingHoursPerMonth() < employeeInfo.minWorkingHoursPerMonth()
                    || employeeInfo.optimalWorkingHoursPerMonth() > employeeInfo.maxWorkingHoursPerMonth()) {
                throw new RuntimeException("invalid employeeInfo optimalWorkingHoursPerMonth");
            }
        }

        CpModel model = new CpModel();

        // Creates shift variables.
        // shifts[(n, d, s)]: employee 'n' works shift type 's' on day 'd'.
        BoolVar[][][] shifts = new BoolVar[input.employees().size()][input.numberOfDays()][input.shiftTypes().size()];
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.numberOfDays(); d++) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    shifts[n][d][s] = model.newBoolVar("shifts_n" + n + "d" + d + "s" + s);
                }
            }
        }

        addHardConstraints(model, input, shifts);
        addOptimization(model, input, shifts);

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(MAX_RUNTIME_IN_SECONDS);

        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.INFEASIBLE) {
            log.warn("Infeasible solution.");
            log.warn(solver.sufficientAssumptionsForInfeasibility().toString());
            return Optional.empty();
        }
        if (status == CpSolverStatus.UNKNOWN) {
            log.warn("To little time to solve problem.");
            return Optional.empty();
        }
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            HashMap<Integer, List<AlgorithmOutput.ShiftTypeDayPair>> assignmentOfEmployeesToShifts = new HashMap<>();
            for (int n = 0; n < input.employees().size(); n++) {
                for (int d = 0; d < input.numberOfDays(); d++) {
                    for (int s = 0; s < input.shiftTypes().size(); s++) {
                        boolean isAssigned = solver.booleanValue(shifts[n][d][s]);
                        if (isAssigned) {
                            if (!assignmentOfEmployeesToShifts.containsKey(n)) {
                                List<AlgorithmOutput.ShiftTypeDayPair> shiftTypeDayPairs = new ArrayList<>();
                                assignmentOfEmployeesToShifts.put(n, shiftTypeDayPairs);
                            }
                            assignmentOfEmployeesToShifts.get(n).add(new AlgorithmOutput.ShiftTypeDayPair(s, d));
                        }
                    }
                }
            }

            AlgorithmOutput output =
                    new AlgorithmOutput(assignmentOfEmployeesToShifts, status == CpSolverStatus.OPTIMAL);
            return Optional.of(output);
        }

        return Optional.empty();
    }

    private static void addHardConstraints(CpModel model, AlgorithmInput input, BoolVar[][][] shifts) {
        // One Shift Per Day - Each employee works at most one shift per day.
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.numberOfDays(); d++) {
                List<Literal> possibleShiftsOnDay =
                        new ArrayList<>(Arrays.asList(shifts[n][d]).subList(0, input.shiftTypes().size()));
                model.addAtMostOne(possibleShiftsOnDay);
            }
        }

        // Shift Compatability - Employees work only shifts they agreed to.
        for (int n = 0; n < input.employees().size(); n++) {
            List<Integer> worksShift = input.employees().get(n).worksShiftTypes();
            for (int s = 0; s < input.shiftTypes().size(); s++) {
                boolean canWorkShift = worksShift.contains(s);
                for (int d = 0; d < input.numberOfDays(); d++) {
                    if (!canWorkShift) model.addEquality(shifts[n][d][s], 0);
                }
            }
        }

        // Maximum and Minimum Monthly Hours - Employees cannot work less/more than max/min working hours
        for (int n = 0; n < input.employees().size(); n++) {
            List<LinearExpr> monthlyHours = new ArrayList<>();
            for (int d = 0; d < input.numberOfDays(); d++) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    // Multiplies the shift variable by its duration to get the hours worked
                    LinearExpr shiftHours = LinearExpr.term(shifts[n][d][s], input.shiftTypes().get(s).duration());
                    monthlyHours.add(shiftHours);
                }
            }
            // Sum up all the hours worked by the employee over the month
            LinearExpr totalMonthlyHours = LinearExpr.sum(monthlyHours.toArray(new LinearExpr[0]));
            // Constraint to ensure total monthly hours does not exceed the maximum allowed for each employee
            model.addLessOrEqual(totalMonthlyHours, input.employees().get(n).maxWorkingHoursPerMonth());
            model.addGreaterOrEqual(totalMonthlyHours, input.employees().get(n).minWorkingHoursPerMonth());
        }

        // Holidays - Employees do not work on holidays
        for (int n = 0; n < input.employees().size(); n++) {
            Set<Integer> holidays = input.employees().get(n).holidays();
            for (int d = 0; d < input.numberOfDays(); d++) {
                boolean isHoliday = holidays.contains(d);
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    if (isHoliday) model.addEquality(shifts[n][d][s], 0);
                }
            }
        }

        // Staffing Level Per Day/Nighttime - There have to always be at least day/nighttimeRequiredPeople present
        List<LinearExpr[]> timeSlotsPerDay = new ArrayList<>();
        for (int d = 0; d < input.numberOfDays(); d++) {
            // 1 slot for every half hour
            LinearExpr[] timeSlots = IntStream.range(0, 48)
                    .mapToObj(i -> LinearExpr.constant(0))
                    .toArray(LinearExpr[]::new);
            timeSlotsPerDay.add(timeSlots);
        }
        for (int d = 0; d < input.numberOfDays(); d++) {
            // TODO #86: handle first day of the month (i.e. include shifts that carry over to the next day)
            LinearExpr[] timeSlots = timeSlotsPerDay.get(d);
            for (int s = 0; s < input.shiftTypes().size(); s++) {
                List<LinearExpr> employeesWorkingShift = new ArrayList<>();
                for (int n = 0; n < input.employees().size(); n++) {
                    employeesWorkingShift.add(LinearExpr.term(shifts[n][d][s], 1));
                }
                LinearExpr numOfEmployeesWorkingShift =
                        LinearExpr.sum(employeesWorkingShift.toArray(LinearExpr[]::new));

                ShiftTypeInfo shiftTypeInfo = input.shiftTypes().get(s);
                int startIndex = timeToSlotIndex(shiftTypeInfo.startTime());
                for (int slot = startIndex; slot - startIndex <= shiftTypeInfo.duration() * 2; slot++) {
                    if (slot < timeSlots.length) {
                        timeSlots[slot] = LinearExpr.sum(
                                new LinearArgument[] {timeSlots[slot], numOfEmployeesWorkingShift}
                        );
                        continue;
                    }
                    // we have carry over i.e. a shift starts on the current day and ends on the next day
                    if (d + 1 >= input.numberOfDays()) {
                        // carry over to next month -> ignored
                        break;
                    }
                    LinearExpr[] timeSlotsNextDay = timeSlotsPerDay.get(d + 1);
                    int slotNextDay = slot % timeSlots.length;
                    timeSlotsNextDay[slotNextDay] = LinearExpr.sum(
                            new LinearArgument[] {timeSlotsNextDay[slotNextDay], numOfEmployeesWorkingShift}
                    );
                    break;
                }
            }

            for (int slot = 0; slot < 24; slot++) {
                model.addGreaterOrEqual(timeSlots[slot], input.daytimeRequiredPeople());
            }
            for (int slot = 24; slot < 48; slot++) {
                model.addGreaterOrEqual(timeSlots[slot], input.nighttimeRequiredPeople());
            }
        }
    }

    /**
     * Converts the time to a slot. Days are split into 30min slots, where 8:00-8:30 is slot 0, 8:30-9:00 is slot 2, ...
     *
     * @param time
     * @return the index of the slot. int between 0 and 47 (since 24h per day; times 2 for 30min)
     */
    private static int timeToSlotIndex(LocalTime time) {
        if (!time.isBefore(LocalTime.of(8, 0))) {
            return time.minusHours(8).getHour() * 2;
        }
        // Time 0:00 - 8:00 loops around and is at the end
        return 31 + time.getHour() * 2;
    }

    private static void addOptimization(CpModel model, AlgorithmInput input, BoolVar[][][] shifts) {
        LinearExprBuilder objective = LinearExpr.newBuilder();

        // Make sure employees work hours close to their working time
        for (int n = 0; n < input.employees().size(); n++) {
            List<LinearExpr> monthlyHours = new ArrayList<>();
            for (int d = 0; d < input.numberOfDays() - 1; d++) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    LinearExpr shiftHours = LinearExpr.term(shifts[n][d][s], input.shiftTypes().get(s).duration());
                    monthlyHours.add(shiftHours);
                }
            }
            LinearExpr totalMonthlyHours = LinearExpr.sum(monthlyHours.toArray(new LinearExpr[0]));
            LinearExpr optimalHours = LinearExpr.constant(input.employees().get(n).optimalWorkingHoursPerMonth());
            IntVar deviation = model.newIntVar(0, Integer.MAX_VALUE, "deviation_workingHours_" + n);

            // Add constraints to link the deviation with the actual and optimal hours
            model.addAbsEquality(deviation,
                    LinearExpr.sum(new LinearArgument[] {totalMonthlyHours, LinearExpr.term(optimalHours, -1)}));

            // Add the deviation to the objective
            objective.add(deviation);
        }

        // Make sure employees work the same shift type as much as possible
        for (int n = 0; n < input.employees().size(); n++) {
            LinearExpr[] shiftTypeCounts = new LinearExpr[input.shiftTypes().size()];
            for (int s = 0; s < input.shiftTypes().size(); s++) {
                int finalS = s;
                shiftTypeCounts[s] = LinearExpr.sum(Arrays.stream(shifts[n])
                        .map(el -> el[finalS])
                        .toArray(BoolVar[]::new));
            }

            // Make sure that shiftTypeCounts is shifted towards 1 value. I.e [10,0,0] is good; [4,3,3] is bad.

            // Calculate the average shift count.
            // addDivisionEquality doesn't work since we use IntVars and the result might be a float.
            // So the below is more or less rounding.
            LinearExpr totalShifts = LinearExpr.sum(shiftTypeCounts);
            IntVar avgShiftCount = model.newIntVar(0, Integer.MAX_VALUE, "avgShiftCount_" + n);
            LinearExpr useToCalcAverage = LinearExpr.term(avgShiftCount, input.shiftTypes().size());
            model.addGreaterOrEqual(totalShifts, useToCalcAverage);
            model.addLessOrEqual(totalShifts, LinearExpr.sum(
                    new LinearArgument[] {useToCalcAverage, LinearExpr.constant(input.shiftTypes().size())}));

            int finalN = n;
            LinearExpr[] deviationFromShift = IntStream.range(0, input.shiftTypes().size())
                    .mapToObj(s -> {
                        IntVar deviationFromAverage =
                                model.newIntVar(0, Integer.MAX_VALUE, "deviation_sameShift_" + finalN + "_" + s);
                        model.addAbsEquality(deviationFromAverage,
                                LinearExpr.sum(
                                        new LinearArgument[] {shiftTypeCounts[s], LinearExpr.term(avgShiftCount, -1)}));
                        return LinearExpr.sum(
                                new LinearArgument[] {totalShifts, LinearExpr.term(deviationFromAverage, -1)});
                    })
                    .toArray(LinearExpr[]::new);

            objective.addSum(deviationFromShift);
        }

        // OffDays - Employees should not have to work on off days
        for (int n = 0; n < input.employees().size(); n++) {
            List<LinearExpr> workingOnOffDays = new ArrayList<>();
            for (Integer offDay : input.employees().get(n).offDays()) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    // high coeff means high importance for this optimization
                    workingOnOffDays.add(LinearExpr.term(shifts[n][offDay][s], 100));
                }
            }
            objective.addSum(workingOnOffDays.toArray(LinearExpr[]::new));
        }

        model.minimize(objective);
    }
}
