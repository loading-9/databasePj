package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "technician")
public class Technician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "technician_id")
    private Long technicianId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "income")
    private double income;


    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL)
    private List<RepairRecord> repairRecords = new ArrayList<>();

}