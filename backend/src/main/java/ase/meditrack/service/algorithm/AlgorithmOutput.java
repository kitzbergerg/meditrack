package ase.meditrack.service.algorithm;

import java.util.HashMap;
import java.util.List;

public record AlgorithmOutput(
        // key is employee, value is all shifts assigned to the employee
        HashMap<Integer, List<ShiftTypeDayPair>> assignmentOfEmployeesToShifts,
        boolean isOptimal
) {
    public record ShiftTypeDayPair(
            Integer shiftType,
            Integer day
    ) {
    }
}
