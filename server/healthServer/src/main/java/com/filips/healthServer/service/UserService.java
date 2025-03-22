package com.filips.healthServer.service;

import com.filips.healthServer.dto.UserDTO;
import com.filips.healthServer.mapper.UserMapper;
import com.filips.healthServer.model.Users;
import com.filips.healthServer.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Users saveUser(UserDTO dto) {
        Users user = UserMapper.INSTANCE.toEntity(dto);
        return userRepository.save(user);
    }
}