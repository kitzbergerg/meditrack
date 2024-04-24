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
     * <br>
     * Goals:
     * Generate a monthly possible shift schedule
     * <br>
     * Input:
     * [x] Nurses
     * [x] Days
     * [x] Shift-types
     * <br>
     * Constraints:
     * [x] Employees work at most one shift per day
     * [x] Required nurses per shift
     * [x] Off-days/Holidays
     * [x] Required total staffing levels
     * [x] Max/Min hours worked
     * [x] Max consecutive shifts before off-day
     * <br>
     * Optimizations:
     * [x] Maximize same shifts in a row
     */
    public static void main(String[] args) {

        Loader.loadNativeLibraries();
        final int numNurses = 5;
        final int numDays = 30;
        final int numShifts = 3;

        final int[] allNurses = IntStream.range(0, numNurses).toArray();
        final int[] allDays = IntStream.range(0, numDays).toArray();
        final int[] allShifts = IntStream.range(0, numShifts).toArray(); // would hold the different shift types

        Map<Integer, List<Integer>> nurseShiftCompatibility = new HashMap<>(); // Which nurses can work which shifts
        nurseShiftCompatibility.put(0, Arrays.asList(0,1,2));
        nurseShiftCompatibility.put(1, Arrays.asList(0,1,2));
        nurseShiftCompatibility.put(2, Arrays.asList(0,1,2));
        nurseShiftCompatibility.put(3, Arrays.asList(0,1,2));
        nurseShiftCompatibility.put(4, Arrays.asList(0,1,2));

        int[] minNursesPerShift = {1, 1, 0}; // Minimum nurses for shifts

        int[] shiftDurations = {12, 12, 6}; // Duration of each shift type in hours

        Map<Integer, Integer> nurseMonthlyMaxHours = new HashMap<>(); // Max hours worked per nurse
        nurseMonthlyMaxHours.put(0, 210);
        nurseMonthlyMaxHours.put(1, 210);
        nurseMonthlyMaxHours.put(2, 210);
        nurseMonthlyMaxHours.put(3, 210);
        nurseMonthlyMaxHours.put(4, 210);

        Map<Integer, Integer> nurseMonthlyMinHours = new HashMap<>(); // Min hours worked per nurse
        nurseMonthlyMinHours.put(0, 180);
        nurseMonthlyMinHours.put(1, 180);
        nurseMonthlyMinHours.put(2, 180);
        nurseMonthlyMinHours.put(3, 180);
        nurseMonthlyMinHours.put(4, 180);

        // max consecutive work days and staffing level
        int maxConsecutiveWorkDays = 4;
        int minTotalNursesPerDay = 3;

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
            List<BoolVar> dailyNurses = new ArrayList<>(); // To track all nurses working on day 'd'
            for (int s : allShifts) {
                List<BoolVar> nurses = new ArrayList<>();
                for (int n : allNurses) {
                    if (nurseShiftCompatibility.get(n).contains(s)) {
                        BoolVar shift = (BoolVar) shifts[n][d][s];
                        nurses.add(shift);
                        if (!dailyNurses.contains(shift)) { // Ensure each nurse is only added once per day
                            dailyNurses.add(shift);
                        }
                    }
                }
                LinearExpr nurseSum = LinearExpr.sum(nurses.toArray(new BoolVar[0]));
                // Add a constraint that at least minNursesPerShift[s] must be true
                model.addGreaterOrEqual(nurseSum, minNursesPerShift[s]);
            }
            // Add a constraint to ensure that the total number of nurses per day meets the minimum
            LinearExpr totalNursesPerDay = LinearExpr.sum(dailyNurses.toArray(new BoolVar[0]));
            model.addGreaterOrEqual(totalNursesPerDay, minTotalNursesPerDay);
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
        solver.getParameters().setMaxTimeInSeconds(3); // algo doesn't complete without maxTime
        CpSolverStatus status = solver.solve(model);

        int[] nurseHours = new int[numNurses];
        int[][] worksToday = new int[numNurses][numDays];

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.printf("Solution:%n");
            for (int d : allDays) {
                System.out.printf("Day %d%n", d);
                for (int n : allNurses) {
                    worksToday[n][d] = -1;
                    for (int s : allShifts) {
                        if (solver.booleanValue(shifts[n][d][s])) {
                            nurseHours[n] += shiftDurations[s];
                            worksToday[n][d] = s;
                            System.out.printf("  Nurse %d works shift %d.%n", n, s);
                        }
                    }
                }
            }
        } else {
            System.out.printf("No optimal solution found !");
        }

        // Statistics
        System.out.println("Statistics");
        System.out.printf("  conflicts: %d%n", solver.numConflicts());
        System.out.printf("  branches : %d%n", solver.numBranches());
        System.out.printf("  wall time: %f s%n", solver.wallTime());

        for (int i = 0; i < numNurses; i++) {
            System.out.print("Nurse " + i + ": \n" + "Total working hours: " + nurseHours[i] + "\nSchedule: ");
            for (int j = 0; j < numDays; j++) {
                if (j % 7 == 0) { // weeks
                    System.out.print("|");
                }
                if (worksToday[i][j] == -1){ // off-day
                    System.out.print("-");
                } else { // index of shift
                    System.out.print(worksToday[i][j]);
                }
            }
            System.out.println("\n");
        }
    }
}

