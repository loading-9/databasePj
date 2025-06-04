package com.example.dto;

public record MaterialDTO(
        Long materialId,
        Long workOrderId,
        String materialName,
        Double quantity,
        Double unitPrice,
        Double totalPrice
) {}