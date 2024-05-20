package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.PreferencesDto;
import ase.meditrack.model.entity.Preferences;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PreferencesMapper {

    @Named("toDto")
    default PreferencesDto toDto(Preferences preferences) {
        return new PreferencesDto(
                preferences.getId(),
                preferences.getOffDays()
        );
    }

    default Preferences fromDto(PreferencesDto dto) {
        Preferences preferences = new Preferences();

        preferences.setId(dto.id());
        preferences.setOffDays(dto.offDays());

        User user = new User();
        user.setId(dto.id());
        preferences.setUser(user);

        return preferences;
    }

    @IterableMapping(qualifiedByName = "toDto")
    List<PreferencesDto> toDtoList(List<Preferences> preferences);
}
