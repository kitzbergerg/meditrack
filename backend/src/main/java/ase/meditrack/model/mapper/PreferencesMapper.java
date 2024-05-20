package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.PreferencesDto;
import ase.meditrack.model.entity.Preferences;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = EntityUuidMapper.class)
public interface PreferencesMapper {

    @Named("toDto")
    PreferencesDto toDto(Preferences preferences);

    Preferences fromDto(PreferencesDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<PreferencesDto> toDtoList(List<Preferences> preferences);
}
