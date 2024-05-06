package ase.meditrack.algorithm;

import ase.meditrack.service.algorithm.AlgorithmInput;
import ase.meditrack.service.algorithm.SchedulingSolver;
import org.apache.commons.lang.math.IntRange;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SolverTest {

    @Test
    void firstTest() {
        AlgorithmInput input = new AlgorithmInput(List.of(1, 2, 3), Arrays.stream(new IntRange(1, 28).toArray()).boxed().toList(), List.of(1, 2, 3));
        var solution = SchedulingSolver.solve(input);
        System.out.println(solution.get());
    }
}
