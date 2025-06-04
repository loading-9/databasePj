package com.example.dto;


public record RegisterRequest(String username,
                              String password,
                              String name,
                              String contactInfo) {
}