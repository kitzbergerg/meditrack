package ase.meditrack.util;

import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Team;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultTestCreator {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private RoleRepository roleRepository;

    public Team createDefaultTeam() {
        Team team = new Team();
        team.setName("default-team");
        team.setWorkingHours(0);
        team.setHardConstraints(new HardConstraints(
                null,
                0,
                0,
                0,
                0,
                0,
                team
        ));
        return teamRepository.save(team);
    }

    public Role createDefaultRole(Team team) {
        Role role = new Role();
        role.setName("default-role");
        role.setTeam(team);
        role.setAllowedFlextimeTotal(0);
        role.setAllowedFlextimePerMonth(0);
        role.setDaytimeRequiredPeople(0);
        role.setNighttimeRequiredPeople(0);
        return roleRepository.save(role);
    }
}
