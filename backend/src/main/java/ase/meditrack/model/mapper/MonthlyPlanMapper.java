package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MonthlyPlanMapper {

    @Named("toDto")
    @Mapping(source = "team.id", target = "team")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    default UUID shiftToId(Shift entity) {
        return entity != null ? entity.getId() : null;
    }

    @Mapping(source = "team", target = "team.id")
    MonthlyPlan fromDto(MonthlyPlanDto dto);

    default Shift idToShift(UUID id) {
        Shift entity = new Shift();
        entity.setId(id);
        return entity;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
