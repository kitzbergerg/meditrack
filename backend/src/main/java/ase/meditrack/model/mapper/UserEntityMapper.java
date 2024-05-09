package ase.meditrack.model.mapper;

import ase.meditrack.model.dto.UserEntityDto;
import ase.meditrack.model.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface UserEntityMapper {

    @Named("toDto")
    UserEntityDto toDto(User entity);

    User fromDto(UserEntityDto dto);

    @IterableMapping(qualifiedByName = "toDto")
    List<UserEntityDto> toDtoList(List<User> entityList);
}
