package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.entity.ShiftSwap;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface ShiftSwapMapper {

    @Named("toDto")
    ShiftSwapDto toDto(ShiftSwap shiftSwap);

    ShiftSwap fromDto(ShiftSwapDto shiftSwapDto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftSwapDto> toDtoList(List<ShiftSwap> shiftSwaps);
}
