package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftSwapDto;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ShiftSwapMapper {

    @Named("toDto")
    default ShiftSwapDto toDto(ShiftSwap shiftSwap) {
        return new ShiftSwapDto(
                shiftSwap.getId(),
                shiftSwap.getSwapRequestingUser() != null ? shiftSwap.getSwapRequestingUser().getId() : null,
                shiftSwap.getRequestedShift() != null ? shiftSwap.getRequestedShift().getId() : null,
                shiftSwap.getSwapSuggestingUsers() != null ?
                        shiftSwap.getSwapSuggestingUsers().stream().map(User::getId).collect(Collectors.toList()) : null,
                shiftSwap.getSuggestedShifts() != null ?
                        shiftSwap.getSuggestedShifts().stream().map(Shift::getId).collect(Collectors.toList()) : null
        );
    }

    default ShiftSwap fromDto(ShiftSwapDto shiftSwapDto) {
        ShiftSwap shiftSwap = new ShiftSwap();

        shiftSwap.setId(shiftSwapDto.id());

        if (shiftSwapDto.requestedShift() != null) {
            Shift shift = new Shift();
            shift.setId(shiftSwapDto.requestedShift());
            shiftSwap.setRequestedShift(shift);
        }

        if (shiftSwapDto.swapRequestingUser() != null) {
            User user = new User();
            user.setId(shiftSwapDto.swapRequestingUser());
            shiftSwap.setSwapRequestingUser(user);
        }

        if (shiftSwapDto.swapSuggestingUsers() != null) {
            shiftSwap.setSwapSuggestingUsers(shiftSwapDto.swapSuggestingUsers().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).collect(Collectors.toList()));
        }

        if (shiftSwapDto.suggestedShifts() != null) {
            shiftSwap.setSuggestedShifts(shiftSwapDto.suggestedShifts().stream().map(id -> {
                Shift shift = new Shift();
                shift.setId(id);
                return shift;
            }).collect(Collectors.toList()));
        }

        return shiftSwap;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftSwapDto> toDtoList(List<ShiftSwap> shiftSwaps);
}
