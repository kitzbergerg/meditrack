package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.DailyUserShiftDto;
import ase.meditrack.model.dto.DayShiftsDto;
import ase.meditrack.model.dto.MonthlyPlanDto;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



@Mapper(uses = {UserMapper.class, ShiftTypeMapper.class})
public interface ScheduleMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(target = "dayShifts", expression = "java(mapShifts(monthlyPlan.getShifts(), userMapper, shiftTypeMapper))")
    MonthlyPlanDto toDto(MonthlyPlan monthlyPlan);

    // TODO: Remove mockedUserDtos
    default List<DayShiftsDto> mapShifts(List<Shift> shifts, @Context UserMapper userMapper, @Context ShiftTypeMapper shiftTypeMapper) {
        return shifts.stream()
                .collect(Collectors.groupingBy(shift -> shift.getDate().getDayOfMonth()))
                .entrySet().stream()
                .map(entry -> {
                    int day = entry.getKey();
                    List<Shift> dayShifts = entry.getValue();
                    List<DailyUserShiftDto> userShiftDTOs = dayShifts.stream()
                            .map(shift -> new DailyUserShiftDto(userMapper.toMockedUserDto(shift.getUsers().get(0)), shiftTypeMapper.toSimpleDto(shift.getShiftType())))
                            .collect(Collectors.toList());
                    return new DayShiftsDto(day, userShiftDTOs);
                })
                .sorted(Comparator.comparingInt(DayShiftsDto::day))
                .collect(Collectors.toList());
    }

    @Mapping(source = "users", target = "user")
    @Mapping(source = "shiftType", target = "shiftType")
    DailyUserShiftDto toDTO(Shift shift);
}
