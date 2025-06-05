package com.example.controller;

import com.example.dto.*;
import com.example.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/work-orders/{workOrdersId}/feedback")
    public ApiResponse<FeedbackDTO> getFeedBack(@PathVariable Long workOrdersId) {
        return vehicleService.getFeedBack(workOrdersId);
    }



}
