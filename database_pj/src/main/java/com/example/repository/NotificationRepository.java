package com.example.repository;

import com.example.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByTechnicianId(Long technicianId);

    void removeNotificationByNotificationId(Long notificationId);
}
