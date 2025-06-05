package com.example.dto;

import com.example.entity.Vehicle;

public record VehicleResponse(Long vehicleId, String licencePlate, String vehicleType, String brand, int manufactureYear, Long userId) {
    public VehicleResponse(Vehicle vehicle) {
        this(vehicle.getVehicleId(), vehicle.getLicensePlate(), vehicle.getVehicleType(), vehicle.getBrand(), vehicle.getManufactureYear(), vehicle.getUser().getUserId());
    }
}
