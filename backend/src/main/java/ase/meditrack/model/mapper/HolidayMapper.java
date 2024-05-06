package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.Holiday;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HolidayMapper {

    @Named("toDto")
    HolidayDto toDto(Holiday entity);

    Holiday fromDto(HolidayDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<HolidayDto> toDtoList(List<Holiday> entityList);
}
