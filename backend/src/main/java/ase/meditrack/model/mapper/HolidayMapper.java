package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.Holiday;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HolidayMapper {

    @Named("toDto")
    @Mapping(source = "user.id", target = "user")
    HolidayDto toDto(Holiday holiday);

    @Mapping(source = "user", target = "user.id")
    Holiday fromDto(HolidayDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<HolidayDto> toDtoList(List<Holiday> holidays);
}
