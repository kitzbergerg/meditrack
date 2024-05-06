package ase.meditrack.service.algorithm;

import java.util.HashMap;
import java.util.List;

public record AlgorithmOutput(
        // key is employee id, value is all shifts assigned to the employee
        HashMap<Integer, List<ShiftTypeDatePair>> assignmentOfEmployeesToShifts,
        boolean isOptimal
) {
    public record ShiftTypeDatePair(
            Integer shiftType,
            Integer date
    ) {
    }
}
