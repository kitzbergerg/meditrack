package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.PreferencesDto;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PreferencesMapper {

    @Named("toDto")
    PreferencesDto toDto(Preferences preferences);

    @Mapping(source = "id", target = "user.id")
    Preferences fromDto(PreferencesDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<PreferencesDto> toDtoList(List<Preferences> preferences);
}
