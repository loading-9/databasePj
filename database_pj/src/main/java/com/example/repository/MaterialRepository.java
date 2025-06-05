package com.example.repository;

import com.example.entity.Material;
import com.example.entity.Technician;
import com.example.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByWorkOrder(WorkOrder workOrder);
}
