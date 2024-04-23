package org.example;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Main {
    /**
     * Scheduling Algorithm - Google OR-Tools
     * <p>
     * Goals:
     * Generate a monthly possible shift schedule
     * <p>
     * Input:
     * [x] Shift-types
     * <p>
     * Constraints:
     * [x] Employees work at most one shift per day
     * [x] Required roles per shift
     * [] Required total staffing levels (day/night)
     */
    public static void main(String[] args) {

        Loader.loadNativeLibraries();
        final int numNurses = 3;
        final int numDays = 10;
        final int numShifts = 3;

        final int[] allNurses = IntStream.range(0, numNurses).toArray();
        final int[] allDays = IntStream.range(0, numDays).toArray();
        final int[] allShifts = IntStream.range(0, numShifts).toArray(); // holds the different shift types

        Map<Integer, List<Integer>> nurseShiftCompatibility = new HashMap<>();
        nurseShiftCompatibility.put(0, Arrays.asList(0,1)); // Nurse 1 can work shifts 0 and 1
        nurseShiftCompatibility.put(1, Arrays.asList(1,2)); // Nurse 2 can work shifts 1 and 2
        nurseShiftCompatibility.put(2, Arrays.asList(2)); // Nurse 3 can work shifts 2

        int[] minNursesPerShift = {1, 0, 2}; // Minimum nurses for shifts 1-3

        // Creates the model.
        CpModel model = new CpModel();

        // Creates shift variables.
        // shifts[(n, d, s)]: nurse 'n' works shift 's' on day 'd'.
        Literal[][][] shifts = new Literal[numNurses][numDays][numShifts];
        for (int n : allNurses) {
            for (int d : allDays) {
                for (int s : allShifts) {
                    shifts[n][d][s] = model.newBoolVar("shifts_n" + n + "d" + d + "s" + s);
                    if (!nurseShiftCompatibility.get(n).contains(s)) {
                        model.addBoolOr(new Literal[]{shifts[n][d][s].not()}); // constraint so nurses only get assigned suitable shifts
                    }
                }
            }
        }

        // Each shift is assigned to at least the minimum required amount - defined in minNursesPerShift
        for (int d : allDays) {
            for (int s : allShifts) {
                List<BoolVar> nurses = new ArrayList<>();
                for (int n : allNurses) {
                    if (nurseShiftCompatibility.get(n).contains(s)) {
                        nurses.add((BoolVar) shifts[n][d][s]);
                    }
                }
                LinearExpr nurseSum = LinearExpr.sum(nurses.toArray(new BoolVar[0]));
                // Add a constraint that at least minNursesPerShift[s] must be true
                model.addGreaterOrEqual(nurseSum, minNursesPerShift[s]);
            }
        }

        // Each nurse works at most one shift per day.
        for (int n : allNurses) {
            for (int d : allDays) {
                List<Literal> work = new ArrayList<>();
                for (int s : allShifts) {
                    work.add(shifts[n][d][s]);
                }
                model.addAtMostOne(work);
            }
        }

        // Creates a solver and solves the model.
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.printf("Solution:%n");
            for (int d : allDays) {
                System.out.printf("Day %d%n", d);
                for (int n : allNurses) {
                    for (int s : allShifts) {
                        if (solver.booleanValue(shifts[n][d][s])) {
                            System.out.printf("  Nurse %d works shift %d .%n", n, s);
                        }
                    }
                }
            }
        } else {
            System.out.printf("No optimal solution found !");
        }
        // Statistics.
        System.out.println("Statistics");
        System.out.printf("  conflicts: %d%n", solver.numConflicts());
        System.out.printf("  branches : %d%n", solver.numBranches());
        System.out.printf("  wall time: %f s%n", solver.wallTime());
    }
}

