package com.filips.healthServer.service;

import com.filips.healthServer.model.Users;
import com.filips.healthServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    public Users getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}