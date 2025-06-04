package com.example.dto;

import com.example.entity.JobType;

public record TechnicianRegisterRequest(String username, String password, String name, JobType jobType, double hourlyRate, String contactInfo, double income) {
}
