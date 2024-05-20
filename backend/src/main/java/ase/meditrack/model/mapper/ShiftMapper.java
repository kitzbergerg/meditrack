package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ShiftMapper {

    @Named("toDto")
    @Mapping(source = "monthlyPlan.id", target = "monthlyPlan")
    @Mapping(source = "shiftType.id", target = "shiftType")
    @Mapping(source = "requestedShiftSwap.id", target = "requestedShiftSwap")
    ShiftDto toDto(Shift shift);

    default UUID userToId(User entity) {
        return entity != null ? entity.getId() : null;
    }

    default UUID shiftSwapToId(ShiftSwap entity) {
        return entity != null ? entity.getId() : null;
    }

    @Mapping(source = "monthlyPlan", target = "monthlyPlan.id")
    @Mapping(source = "shiftType", target = "shiftType.id")
    @Mapping(source = "requestedShiftSwap", target = "requestedShiftSwap.id")
    Shift fromDto(ShiftDto shiftDto);

    default User idToUser(UUID id) {
        User entity = new User();
        entity.setId(id);
        return entity;
    }

    default ShiftSwap idToShiftSwap(UUID id) {
        ShiftSwap entity = new ShiftSwap();
        entity.setId(id);
        return entity;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftDto> toDtoList(List<Shift> shifts);
}
