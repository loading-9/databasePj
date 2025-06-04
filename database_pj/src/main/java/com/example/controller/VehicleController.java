package com.example.controller;

import com.example.dto.ApiResponse;
import com.example.dto.VehicleResponse;
import com.example.dto.VehicleSubmitRequest;
import com.example.dto.WorkOrderResponse;
import com.example.dto.WorkOrderSubmitRequest;
import com.example.service.VehicleService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials = "true")
@RestController
@RequestMapping("/api/user/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }





}
