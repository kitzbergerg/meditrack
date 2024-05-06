package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.entity.ShiftType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ShiftTypeMapper {

    @Named("toDto")
    ShiftTypeDto toDto(ShiftType entity);

    ShiftType fromDto(ShiftTypeDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftTypeDto> toDtoList(List<ShiftType> entityList);
}
