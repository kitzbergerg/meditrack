package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.MonthlyWorkDetailsDto;
import ase.meditrack.model.entity.MonthlyWorkDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(uses = EntityUuidMapper.class)
public interface MonthlyWorkDetailsMapper {

    @Named("toDto")
    @Mapping(target = "userId", source = "details.user.id")
    MonthlyWorkDetailsDto toDto(MonthlyWorkDetails details);

}

