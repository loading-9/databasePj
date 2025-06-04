package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "work_order_technician")
@IdClass(WorkOrderTechnicianId.class)
@Data
@Getter
@Setter
public class WorkOrderTechnician {
    @Id
    @Column(name = "work_order_id")
    private Long workOrderId;

    @Id
    @Column(name = "technician_id")
    private Long technicianId;
}
