package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.Team;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public abstract class MonthlyPlanMapper {

    @Named("toDto")
    public MonthlyPlanDto toDto(MonthlyPlan monthlyPlan) {
        return new MonthlyPlanDto(
                monthlyPlan.getId(),
                monthlyPlan.getMonth(),
                monthlyPlan.getYear(),
                monthlyPlan.getPublished(),
                monthlyPlan.getTeam() != null ? monthlyPlan.getTeam().getId() : null,
                monthlyPlan.getShifts() != null ? monthlyPlan.getShifts().stream()
                        .map(Shift::getId).collect(Collectors.toList()) : null
        );
    }

    public MonthlyPlan fromDto(MonthlyPlanDto dto) {
        MonthlyPlan monthlyPlan = new MonthlyPlan();

        monthlyPlan.setId(dto.id());
        monthlyPlan.setMonth(dto.month());
        monthlyPlan.setYear(dto.year());
        monthlyPlan.setPublished(dto.published());

        if (dto.team() != null) {
            Team team = new Team();
            team.setId(dto.team());
            monthlyPlan.setTeam(team);
        }

        if (dto.shifts() != null) {
            monthlyPlan.setShifts(dto.shifts().stream().map(id -> {
                Shift shift = new Shift();
                shift.setId(id);
                return shift;
            }).collect(Collectors.toList()));
        }

        return monthlyPlan;
    }

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
