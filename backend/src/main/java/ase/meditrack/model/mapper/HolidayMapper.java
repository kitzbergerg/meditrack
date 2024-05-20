package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.HolidayDto;
import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HolidayMapper {

    @Named("toDto")
    default HolidayDto toDto(Holiday holiday) {
        return new HolidayDto(
                holiday.getId(),
                holiday.getStartDate(),
                holiday.getEndDate(),
                holiday.getIsApproved(),
                holiday.getUser() != null ? holiday.getUser().getId() : null
        );
    }

    default Holiday fromDto(HolidayDto dto) {
        Holiday holiday = new Holiday();

        holiday.setId(dto.id());
        holiday.setStartDate(dto.startDate());
        holiday.setEndDate(dto.endDate());
        holiday.setIsApproved(dto.isApproved());

        if (dto.user() != null) {
            User user = new User();
            user.setId(dto.user());
            holiday.setUser(user);
        }

        return holiday;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<HolidayDto> toDtoList(List<Holiday> holidays);
}
