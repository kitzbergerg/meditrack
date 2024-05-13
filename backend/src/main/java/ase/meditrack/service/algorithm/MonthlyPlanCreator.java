package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.User;
import ase.meditrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MonthlyPlanCreator {

    private AlgorithmMapper algorithmMapper;
    private UserRepository userRepository;

    @Autowired
    public MonthlyPlanCreator(AlgorithmMapper algorithmMapper, UserRepository userRepository) {
        this.algorithmMapper = algorithmMapper;
        this.userRepository = userRepository;
    }

    public void createMonthlyPlan(int month, int year, UUID team) {
        // TODO: fetch from db

        List<User> users = userRepository.findAll();

        // TODO: map to algorithm input

        AlgorithmInput input = algorithmMapper.mapToAlgorithmInput(month, year, users, null, null);

        Optional<AlgorithmOutput> output = SchedulingSolver.solve(input);

        // TODO: map from algorithm output

        if(output.isEmpty()) {
            throw new RuntimeException("Couldn't generate schedule");
        }

        List<Shift> shifts = algorithmMapper.mapFromAlgorithmOutput(output.get());


        // TODO: create shifts and monthly plan
    }

    private boolean persistShifts(){
        return false;
    }

}
