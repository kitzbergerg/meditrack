package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface MonthlyPlanMapper {

    @Named("toDto")
    MonthlyPlanDto toDto(MonthlyPlan entity);

    MonthlyPlan fromDto(MonthlyPlanDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<MonthlyPlanDto> toDtoList(List<MonthlyPlan> entityList);
}
