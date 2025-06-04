package com.example.repository;

import com.example.entity.Feedback;
import com.example.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    boolean existsByWorkOrder(WorkOrder workOrder);
}
