package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftTypeDto;
import ase.meditrack.model.entity.*;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public abstract class ShiftTypeMapper {

    @Named("toDto")
    public ShiftTypeDto toDto(ShiftType shiftType) {
        return new ShiftTypeDto(
                shiftType.getId(),
                shiftType.getName(),
                shiftType.getStartTime(),
                shiftType.getEndTime(),
                shiftType.getBreakStartTime(),
                shiftType.getBreakEndTime(),
                shiftType.getType(),
                shiftType.getColor(),
                shiftType.getAbbreviation(),
                shiftType.getTeam() != null ? shiftType.getTeam().getId() : null,
                shiftType.getShifts() != null ? shiftType.getShifts().stream().map(Shift::getId).toList() : null,
                shiftType.getWorkUsers() != null ? shiftType.getWorkUsers().stream().map(User::getId).toList() : null,
                shiftType.getPreferUsers() != null ? shiftType.getPreferUsers().stream().map(User::getId).toList() : null
        );
    }

    public ShiftType fromDto(ShiftTypeDto dto) {
        ShiftType shiftType = new ShiftType();

        if (dto.id() == null) {
            // id is only null on creation
        } else {
            shiftType.setId(dto.id());
        }

        if (dto.name() != null) {
            shiftType.setName(dto.name());
        }

        if (dto.startTime() != null) {
            shiftType.setStartTime(dto.startTime());
        }

        if (dto.endTime() != null) {
            shiftType.setEndTime(dto.endTime());
        }

        if (dto.breakStartTime() != null) {
            shiftType.setBreakStartTime(dto.breakStartTime());
        }

        if (dto.breakEndTime() != null) {
            shiftType.setBreakEndTime(dto.breakEndTime());
        }

        if (dto.type() != null) {
            shiftType.setType(dto.type());
        }

        if (dto.color() != null) {
            shiftType.setColor(dto.color());
        }

        if (dto.abbreviation() != null) {
            shiftType.setAbbreviation(dto.abbreviation());
        }

        if (dto.team() != null) {
            Team team = new Team();
            team.setId(dto.team());
            shiftType.setTeam(team);
        }

        if (dto.shifts() != null) {
            shiftType.setShifts(dto.shifts().stream().map(id -> {
                Shift shift = new Shift();
                shift.setId(id);
                return shift;
            }).toList());
        }

        if (dto.workUsers() != null) {
            shiftType.setWorkUsers(dto.workUsers().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).toList());
        }

        if (dto.preferUsers() != null) {
            shiftType.setPreferUsers(dto.preferUsers().stream().map(id -> {
                User user = new User();
                user.setId(id);
                return user;
            }).toList());
        }

        return shiftType;
    }

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<ShiftTypeDto> toDtoList(List<ShiftType> shiftTypes);
}
