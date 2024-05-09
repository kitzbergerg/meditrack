package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public abstract class TeamMapper {

    @Named("toDto")
    public TeamDto toDto(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getWorkingHours(),
                team.getUsers() != null ? team.getUsers().stream().map(User::getId).toList() : null,
                team.getHardConstraints() != null ? team.getHardConstraints().getId() : null,
                team.getMonthlyPlans() != null ? team.getMonthlyPlans().stream().map(MonthlyPlan::getId).toList() : null,
                team.getShiftTypes() != null ? team.getShiftTypes().stream().map(ShiftType::getId).toList() : null
        );
    }

    public Team fromDto(TeamDto dto) {
        Team team = new Team();

        if (dto.id() == null) {
            // id is only null on creation
            // userRepresentation.setEnabled(true);
        } else {
            team.setId(dto.id());
        }
        team.setName(dto.name());
        team.setWorkingHours(dto.workingHours());

        if (dto.users() != null) {
            team.setUsers(dto.users().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).toList());
        }

        if (dto.hardConstraints() != null) {
            HardConstraints hardConstraints = new HardConstraints();
            hardConstraints.setId(dto.hardConstraints());
            team.setHardConstraints(hardConstraints);
        }

        if (dto.monthlyPlans() != null) {
            team.setMonthlyPlans(dto.monthlyPlans().stream().map(id -> {
                MonthlyPlan monthlyPlan = new MonthlyPlan();
                monthlyPlan.setId(id);
                return monthlyPlan;
            }).toList());
        }

        if (dto.shiftTypes() != null) {
            team.setShiftTypes(dto.shiftTypes().stream().map(id -> {
                ShiftType shiftType = new ShiftType();
                shiftType.setId(id);
                return shiftType;
            }).toList());
        }

        return team;
    }

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<TeamDto> toDtoList(List<Team> teams);
}
