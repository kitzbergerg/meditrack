package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.dto.SimpleShiftTypeDto;
import ase.meditrack.model.entity.ShiftType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface ShiftTypeMapper {

    @Named("toDto")
    ShiftTypeDto toDto(ShiftType shiftType);

    ShiftType fromDto(ShiftTypeDto dto);

    @Named("toSimpleDto")
    SimpleShiftTypeDto toSimpleDto(ShiftType canWorkShiftType);

    @IterableMapping(qualifiedByName = "toSimpleDto")
    List<SimpleShiftTypeDto> toSimpleDtoList(List<ShiftType> canWorkShiftTypes);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftTypeDto> toDtoList(List<ShiftType> shiftTypes);
}
