package com.example.repository;

import com.example.entity.JobType;
import com.example.entity.WorkOrderTechnician;
import com.example.entity.WorkOrderTechnicianId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderTechnicianRepository extends JpaRepository<WorkOrderTechnician, WorkOrderTechnicianId> {
    List<WorkOrderTechnician> findByWorkOrderId(Integer workOrderId);
    List<WorkOrderTechnician> findByTechnicianId(Integer technicianId);

    @Query("SELECT t.technicianId, COUNT(wt) as taskCount " +
            "FROM Technician t LEFT JOIN WorkOrderTechnician wt ON t.technicianId = wt.technicianId " +
            "WHERE t.jobType = :jobType GROUP BY t.technicianId ORDER BY taskCount ASC")
    List<Object[]> findTechniciansByJobTypeWithTaskCount(JobType jobType);
}