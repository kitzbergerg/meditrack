package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.TeamDto;
import ase.meditrack.model.entity.Team;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TeamMapper {

    @Named("toDto")
    TeamDto toDto(Team entity);

    Team fromDto(TeamDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<TeamDto> toDtoList(List<Team> entityList);
}
