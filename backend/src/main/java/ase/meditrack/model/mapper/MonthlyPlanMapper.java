package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface MonthlyPlanMapper {

    @Named("toDto")
    @Mapping(target = "year", expression = "java(Year.of(monthlyPlan.getYear()))")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    @Mapping(target = "year", expression = "java(dto.year().getValue())")
    MonthlyPlan fromDto(MonthlyPlanDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
