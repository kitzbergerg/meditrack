package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.dto.ShiftScheduleDto;
import ase.meditrack.model.entity.Shift;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface ShiftMapper {

    @Named("toDto")
    ShiftDto toDto(Shift shift);

    Shift fromDto(ShiftDto shiftDto);

    @Mapping(target = "name", source = "shift.shiftType.name")
    @Mapping(target = "color", source = "shift.shiftType.color")
    @Mapping(target = "startTime", source = "shift.shiftType.startTime")
    @Mapping(target = "endTime", source = "shift.shiftType.endTime")
    @Mapping(target = "user", expression = "java(shift.user[0])")
    ShiftScheduleDto toScheduleDto(Shift shift);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftDto> toDtoList(List<Shift> shifts);
}
