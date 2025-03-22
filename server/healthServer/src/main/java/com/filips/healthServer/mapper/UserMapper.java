package com.filips.healthServer.mapper;

import com.filips.healthServer.dto.UserDTO;
import com.filips.healthServer.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(Users user);
    Users toEntity(UserDTO dto);
}
