package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.ShiftOffShiftIdListDto;
import ase.meditrack.model.entity.ShiftOffShiftIdList;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ShiftOffShiftIdListMapper {

    @Named("toDto")
    ShiftOffShiftIdListDto toDto(ShiftOffShiftIdList entity);

    ShiftOffShiftIdList fromDto(ShiftOffShiftIdListDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<ShiftOffShiftIdListDto> toDtoList(List<ShiftOffShiftIdList> entityList);
}
