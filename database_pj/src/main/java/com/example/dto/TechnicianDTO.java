package com.example.dto;

import com.example.entity.JobType;
import com.example.entity.Technician;

public record TechnicianDTO(
        Long technicianId,
        String username,
        String name,
        JobType jobType,
        Double hourlyRate,
        String contactInfo,
        Double income
){
    public TechnicianDTO(Technician technician) {
        this(technician.getTechnicianId(), technician.getUsername(), technician.getName(), technician.getJobType(), technician.getHourlyRate(), technician.getContactInfo(), technician.getIncome());
    }
}