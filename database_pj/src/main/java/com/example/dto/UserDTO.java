package com.example.dto;


import com.example.entity.User;
import com.example.entity.Vehicle;

import java.time.LocalDateTime;
import java.util.List;

public record UserDTO(String username, String name, Long id, String contactInfo, LocalDateTime registerTime) {
    public UserDTO(User user) {
        this(user.getUsername(), user.getName(), user.getUserId(), user.getContactInfo(), user.getRegisterTime());
    }
}