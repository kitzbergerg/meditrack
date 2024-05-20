package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TeamMapper {

    @Named("toDto")
    @Mapping(source = "hardConstraints.id", target = "hardConstraints")
    TeamDto toDto(Team team);

    default UUID userToId(User entity) {
        return entity != null ? entity.getId() : null;
    }

    default UUID monthlyPlanToId(MonthlyPlan entity) {
        return entity != null ? entity.getId() : null;
    }

    default UUID shiftTypesToId(ShiftType entity) {
        return entity != null ? entity.getId() : null;
    }

    @Mapping(source = "hardConstraints", target = "hardConstraints.id")
    Team fromDto(TeamDto dto);

    default User idToUser(UUID id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    default MonthlyPlan idToMonthlyPlan(UUID id) {
        MonthlyPlan monthlyPlan = new MonthlyPlan();
        monthlyPlan.setId(id);
        return monthlyPlan;
    }

    default ShiftType idToShiftTypes(UUID id) {
        ShiftType shiftType = new ShiftType();
        shiftType.setId(id);
        return shiftType;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<TeamDto> toDtoList(List<Team> teams);
}
