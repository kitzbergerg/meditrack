package ase.meditrack.service.algorithm;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SchedulingSolver {
    private static final int MAX_RUNTIME_IN_SECONDS = 10;

    /**
     * @return true if a valid assignment was found; false otherwise
     */
    public Optional<AlgorithmOutput> solve(AlgorithmInput input) {
        CpModel model = new CpModel();

        // TODO: add constraints

        // TODO: add optimization

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(MAX_RUNTIME_IN_SECONDS);

        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.UNKNOWN) {
            log.warn("To little time to solve problem.");
            return Optional.empty();
        }
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            // TODO: parse and store solution
            return Optional.empty();
        }

        return Optional.empty();
    }

}
