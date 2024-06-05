package ase.meditrack.service.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.Literal;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
        if (input.days().size() < minNumberOfDaysInMonth || input.employees().isEmpty()
                || input.shiftTypes().isEmpty()) {
            throw new RuntimeException("invalid input");
        }

        CpModel model = new CpModel();

        // Creates shift variables.
        // shifts[(n, d, s)]: employee 'n' works shift type 's' on day 'd'.
        BoolVar[][][] shifts = new BoolVar[input.employees().size()][input.days().size()][input.shiftTypes().size()];
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.days().size(); d++) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    shifts[n][d][s] = model.newBoolVar("shifts_n" + n + "d" + d + "s" + s);
                }
            }
        }

        addHardConstraints(model, input, shifts);
        addOptimization(model, input);

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(MAX_RUNTIME_IN_SECONDS);

        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.UNKNOWN) {
            log.warn("To little time to solve problem.");
            return Optional.empty();
        }
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            HashMap<Integer, List<AlgorithmOutput.ShiftTypeDayPair>> assignmentOfEmployeesToShifts = new HashMap<>();
            for (int n = 0; n < input.employees().size(); n++) {
                for (int d = 0; d < input.days().size(); d++) {
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
        // TODO #86: add constraints
        // One Shift Per Day - Each employee works at most one shift per day.
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.days().size(); d++) {
                List<Literal> possibleShiftsOnDay =
                        new ArrayList<>(Arrays.asList(shifts[n][d]).subList(0, input.shiftTypes().size()));
                model.addAtMostOne(possibleShiftsOnDay);
            }
        }

        // Shift Compatability - Employees work only shifts they agreed to.
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.days().size(); d++) {
                List<Integer> worksShift = input.employees().get(n).worksShifts();
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    if (!worksShift.contains(s)) model.addEquality(shifts[n][d][s], 0);
                }
            }
        }

        // Maximum Monthly Hours - Employees cannot work more than workingHours + overtime per month
        for (int n = 0; n < input.employees().size(); n++) {
            List<LinearExpr> monthlyHours = new ArrayList<>();
            for (int d = 0; d < input.days().size(); d++) {
                for (int s = 0; s < input.shiftTypes().size(); s++) {
                    // Multiplies the shift variable by its duration to get the hours worked
                    LinearExpr shiftHours = LinearExpr.term(shifts[n][d][s], input.shiftTypes().get(s).duration());
                    monthlyHours.add(shiftHours);
                }
            }
            // Sum up all the hours worked by the employee over the month
            LinearExpr totalMonthlyHours = LinearExpr.sum(monthlyHours.toArray(new LinearExpr[0]));
            // Constraint to ensure total monthly hours does not exceed the maximum allowed for each employee
            // TODO #86: instead of hardcoding 20, get it from hardConstraints and overtime values
            // TODO #86: make sure holidays and off days are considered in this calculation
            model.addLessOrEqual(totalMonthlyHours, input.employees().get(n).workingHours() + 20);
            model.addGreaterOrEqual(totalMonthlyHours, input.employees().get(n).workingHours() / 2);
        }


        // Every shiftType on a day has to have at least one employee
        // TODO #86: this should be changed in the long run
        for (int d = 0; d < input.days().size(); d++) {
            for (int s = 0; s < input.shiftTypes().size(); s++) {
                List<Literal> employeesToShiftTypes = new ArrayList<>();
                for (int n = 0; n < input.employees().size(); n++) {
                    employeesToShiftTypes.add(shifts[n][d][s]);
                }
                model.addAtLeastOne(employeesToShiftTypes);
            }
        }
    }

    private static void addOptimization(CpModel model, AlgorithmInput input) {
        // TODO #86: add optimization
    }

}
