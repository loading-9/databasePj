package com.example.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "work_order")
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_order_id")
    private Long workOrderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;

    @Column(name = "problem")
    private String problem;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "labor_cost")
    private Double laborCost;

    @Column(name = "material_cost")
    private Double materialCost;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "progress")
    private Double progress;

    @Column(name = "progress_description")
    private String progressDescription;


    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL)
    private List<Material> materials = new ArrayList<>();

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL)
    private List<RepairRecord> repairRecords = new ArrayList<>();

    @OneToOne(mappedBy = "workOrder", cascade = CascadeType.ALL)
    private Feedback feedback;
}