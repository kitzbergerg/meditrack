package ase.meditrack.service.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SchedulingSolver {
    static {
        Loader.loadNativeLibraries();
    }

    private static final int MAX_RUNTIME_IN_SECONDS = 10;

    private SchedulingSolver() {
    }

    /**
     * @param input the information the algorithm uses
     * @return AlgorithmOutput if a valid assignment was found; empty otherwise
     */
    public static Optional<AlgorithmOutput> solve(final AlgorithmInput input) {
        final int minNumberOfDaysInMonth = 28;
        if (input.days().size() < minNumberOfDaysInMonth || input.employees().isEmpty() || input.shiftTypes().isEmpty()) {
            throw new RuntimeException("invalid input");
        }

        CpModel model = new CpModel();

        // Creates shift variables.
        // shifts[(n, d, s)]: nurse 'n' works shift type 's' on day 'd'.
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

            AlgorithmOutput output = new AlgorithmOutput(assignmentOfEmployeesToShifts, status == CpSolverStatus.OPTIMAL);
            return Optional.of(output);
        }

        return Optional.empty();
    }

    private static void addHardConstraints(CpModel model, AlgorithmInput input, BoolVar[][][] shifts) {
        // TODO: add constraints
        // Each nurse works at most one shift per day.
        for (int n = 0; n < input.employees().size(); n++) {
            for (int d = 0; d < input.days().size(); d++) {
                List<Literal> possibleShiftsOnDay = new ArrayList<>(Arrays.asList(shifts[n][d]).subList(0, input.shiftTypes().size()));
                model.addAtMostOne(possibleShiftsOnDay);
            }
        }

        // Every shiftType on a day has to have at least one employee
        // TODO: this should be changed in the long run
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
        // TODO: add optimization
    }

}
