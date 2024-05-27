package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.dto.ShiftScheduleDto;
import ase.meditrack.model.entity.Shift;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {EntityUuidMapper.class, UserMapper.class})

public interface ShiftMapper {

    @Named("toDto")
    ShiftDto toDto(Shift shift);

    Shift fromDto(ShiftDto shiftDto);

    @Named("toScheduleDto")
    @Mapping(target = "date", source = "shift.date")
    @Mapping(target = "type", source = "shift.shiftType")
    @Mapping(target = "users", source = "shift.users")
    ShiftScheduleDto toScheduleDto(Shift shift);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftDto> toDtoList(List<Shift> shifts);

    @IterableMapping(qualifiedByName = "toScheduleDto")
    List<ShiftScheduleDto> toScheduleDtoList(List<Shift> shifts);
}
