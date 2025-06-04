
package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "work_order_id", nullable = false)
    private Long workOrderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "technician_id", nullable = false)
    private Long technicianId;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "remind_time", nullable = false)
    private LocalDateTime remindTime;

    @Column(name = "type", nullable = false)
    private String type;
}

