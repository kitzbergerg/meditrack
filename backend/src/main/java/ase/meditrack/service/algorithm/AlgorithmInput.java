package ase.meditrack.service.algorithm;

import java.util.List;

public record AlgorithmInput(
        List<Integer> employees,
        List<Integer> days,
        List<Integer> shiftTypes

        // TODO: add more fields for other constraints
) {
}
