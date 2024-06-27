package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.dto.MonthlyWorkDetailsDto;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.MonthlyWorkDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {EntityUuidMapper.class, ShiftMapper.class})
public interface MonthlyPlanMapper {

    @Named("toDto")
    @Mapping(target = "month", expression = "java(Month.of(monthlyPlan.getMonth()))")
    @Mapping(target = "year", expression = "java(Year.of(monthlyPlan.getYear()))")
    @Mapping(target = "monthlyWorkDetails", source = "monthlyWorkDetails", qualifiedByName = "toMonthlyWorkDetailDto")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    @Named("toMonthlyWorkDetailDto")
    default MonthlyWorkDetailsDto toMonthlyWorkDetailDto(MonthlyWorkDetails monthlyWorkDetail) {
        return new MonthlyWorkDetailsDto(
                monthlyWorkDetail.getUser().getId(),
                monthlyWorkDetail.getHoursShouldWork(),
                monthlyWorkDetail.getHoursActuallyWorked(),
                monthlyWorkDetail.getOvertime()
        );
    }

    @Mapping(target = "month", expression = "java(dto.month().getValue())")
    @Mapping(target = "year", expression = "java(dto.year().getValue())")
    MonthlyPlan fromDto(MonthlyPlanDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> monthlyPlans);
}
