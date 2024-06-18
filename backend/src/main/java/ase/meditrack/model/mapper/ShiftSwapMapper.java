package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.dto.SimpleShiftSwapDto;
import ase.meditrack.model.entity.ShiftSwap;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {EntityUuidMapper.class, ShiftMapper.class})
public interface ShiftSwapMapper {

    @Named("toDto")
    ShiftSwapDto toDto(ShiftSwap shiftSwap);

    ShiftSwap fromDto(ShiftSwapDto shiftSwapDto);

    /*
        TODO: not used

    SimpleShiftSwapDto toSimpleShiftSwapDto(ShiftSwap shiftSwap);

    ShiftSwap fromSimpleShiftSwapDto(SimpleShiftSwapDto simpleShiftSwapDto);

     */

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftSwapDto> toDtoList(List<ShiftSwap> shiftSwaps);
}
