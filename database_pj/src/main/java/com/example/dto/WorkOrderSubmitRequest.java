package com.example.dto;


import com.example.entity.JobType;

public record WorkOrderSubmitRequest(Long vehicleId, String problem, JobType jobType) {
}
