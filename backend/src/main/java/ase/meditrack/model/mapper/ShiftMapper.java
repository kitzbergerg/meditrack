package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftDto;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftSwap;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ShiftMapper {

    @Named("toDto")
    default ShiftDto toDto(Shift shift) {
        return new ShiftDto(
                shift.getId(),
                shift.getDate(),
                shift.getMonthlyPlan() != null ? shift.getMonthlyPlan().getId() : null,
                shift.getShiftType() != null ? shift.getShiftType().getId() : null,
                shift.getUsers() != null ?
                        shift.getUsers().stream().map(User::getId).collect(Collectors.toList()) : null,
                shift.getSuggestedShiftSwaps() != null ?
                        shift.getSuggestedShiftSwaps().stream().map(ShiftSwap::getId).collect(Collectors.toList()) :
                        null,
                shift.getRequestedShiftSwap() != null ? shift.getRequestedShiftSwap().getId() : null
        );
    }

    default Shift fromDto(ShiftDto shiftDto) {
        Shift shift = new Shift();

        shift.setId(shiftDto.id());
        shift.setDate(shiftDto.date());

        if (shiftDto.monthlyPlan() != null) {
            MonthlyPlan monthlyPlan = new MonthlyPlan();
            monthlyPlan.setId(shiftDto.monthlyPlan());
            shift.setMonthlyPlan(monthlyPlan);
        }

        if (shiftDto.shiftType() != null) {
            ShiftType shiftType = new ShiftType();
            shiftType.setId(shiftDto.shiftType());
            shift.setShiftType(shiftType);
        }

        if (shiftDto.users() != null) {
            shift.setUsers(shiftDto.users().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).collect(Collectors.toList()));
        }

        if (shiftDto.suggestedShiftSwaps() != null) {
            shift.setSuggestedShiftSwaps(shiftDto.suggestedShiftSwaps().stream().map(id -> {
                ShiftSwap shiftSwap = new ShiftSwap();
                shiftSwap.setId(id);
                return shiftSwap;
            }).collect(Collectors.toList()));
        }

        if (shiftDto.requestedShiftSwap() != null) {
            ShiftSwap shiftSwap = new ShiftSwap();
            shiftSwap.setId(shiftDto.requestedShiftSwap());
            shift.setRequestedShiftSwap(shiftSwap);
        }

        return shift;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftDto> toDtoList(List<Shift> shifts);
}
