package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.entity.Shift;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ShiftMapper {

    @Named("toDto")
    ShiftDto toDto(Shift entity);

    Shift fromDto(ShiftDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftDto> toDtoList(List<Shift> entityList);
}
