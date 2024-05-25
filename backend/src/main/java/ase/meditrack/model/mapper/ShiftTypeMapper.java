package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.dto.SimpleShiftTypeDto;
import ase.meditrack.model.entity.ShiftType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface ShiftTypeMapper {

    @Named("toDto")
    ShiftTypeDto toDto(ShiftType shiftType);

    ShiftType fromDto(ShiftTypeDto dto);

    @Named("toSimpleDto")
    @Mapping(target = "id", source = "canWorkShiftType.id")
    @Mapping(target = "name", source = "canWorkShiftType.name")
    @Mapping(target = "startTime", source = "canWorkShiftType.startTime")
    @Mapping(target = "endTime", source = "canWorkShiftType.endTime")
    @Mapping(target = "breakStartTime", source = "canWorkShiftType.breakStartTime")
    @Mapping(target = "breakEndTime", source = "canWorkShiftType.breakEndTime")
    @Mapping(target = "type", source = "canWorkShiftType.type")
    @Mapping(target = "color", source = "canWorkShiftType.color")
    @Mapping(target = "abbreviation", source = "canWorkShiftType.abbreviation")
    @Mapping(target = "team", source = "canWorkShiftType.team")
    SimpleShiftTypeDto toSimpleDto(ShiftType canWorkShiftType);

    @IterableMapping(qualifiedByName = "toSimpleDto")
    List<SimpleShiftTypeDto> toSimpleDtoList(List<ShiftType> canWorkShiftTypes);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftTypeDto> toDtoList(List<ShiftType> shiftTypes);
}
