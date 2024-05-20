package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftSwapDto;
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
public interface ShiftSwapMapper {

    @Named("toDto")
    @Mapping(source = "requestedShift.id", target = "requestedShift")
    @Mapping(source = "swapRequestingUser.id", target = "swapRequestingUser")
    ShiftSwapDto toDto(ShiftSwap shiftSwap);

    default UUID userToId(User entity) {
        return entity != null ? entity.getId() : null;
    }

    default UUID shiftToId(Shift entity) {
        return entity != null ? entity.getId() : null;
    }

    @Mapping(source = "requestedShift", target = "requestedShift.id")
    @Mapping(source = "swapRequestingUser", target = "swapRequestingUser.id")
    ShiftSwap fromDto(ShiftSwapDto shiftSwapDto);

    default User idToUser(UUID id) {
        User entity = new User();
        entity.setId(id);
        return entity;
    }

    default Shift idToShift(UUID id) {
        Shift entity = new Shift();
        entity.setId(id);
        return entity;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftSwapDto> toDtoList(List<ShiftSwap> shiftSwaps);
}
