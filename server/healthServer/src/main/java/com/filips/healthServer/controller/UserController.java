package com.filips.healthServer.controller;

import com.filips.healthServer.dto.UserDTO;
import com.filips.healthServer.model.Users;
import com.filips.healthServer.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public Users createUser(@RequestBody UserDTO user) {
        return userService.saveUser(user);
    }
}