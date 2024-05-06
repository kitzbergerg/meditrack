package ase.meditrack.service.algorithm;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MonthlyPlanCreator {

    public void createMonthlyPlan(int month, int year, UUID team) {
        // TODO: fetch from db

        // TODO: map to algorithm input

        SchedulingSolver solver = new SchedulingSolver();
        Optional<AlgorithmOutput> output = solver.solve(null);

        // TODO: map from algorithm output

        // TODO: create shifts and monthly plan
    }

}
