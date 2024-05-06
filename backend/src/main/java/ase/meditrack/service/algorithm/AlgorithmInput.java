package ase.meditrack.service.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public record AlgorithmInput(
        List<Integer> employees,
        List<Integer> days,
        List<Integer> shiftTypes

        // TODO: add more fields for other constraints
) {
}
