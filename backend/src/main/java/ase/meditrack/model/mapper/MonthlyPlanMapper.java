package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {EntityUuidMapper.class, ShiftMapper.class})
public interface MonthlyPlanMapper {

    @Named("toDto")
    @Mapping(target = "month", expression = "java(Month.of(monthlyPlan.getMonth()))")
    @Mapping(target = "year", expression = "java(Year.of(monthlyPlan.getYear()))")
    @Mapping(target = "shifts", source = "monthlyPlan.shifts")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    @Mapping(target = "month", expression = "java(dto.month().getValue())")
    @Mapping(target = "year", expression = "java(dto.year().getValue())")
    MonthlyPlan fromDto(MonthlyPlanDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
