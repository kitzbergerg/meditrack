package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface MonthlyPlanMapper {

    @Named("toDto")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    MonthlyPlan fromDto(MonthlyPlanDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
