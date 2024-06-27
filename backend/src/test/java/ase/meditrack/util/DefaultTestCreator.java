package ase.meditrack.util;

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
        team.setNighttimeRequiredPeople(0);
        team.setDaytimeRequiredPeople(0);
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
        role.setWorkingHours(40);
        role.setMaxWeeklyHours(30);
        role.setMaxConsecutiveShifts(5);
        return roleRepository.save(role);
    }
}
