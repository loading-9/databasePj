package com.example.repository;


import com.example.dto.TechnicianStatsDTO;
import com.example.dto.VehicleTypeStatsDTO;
import com.example.entity.User;
import com.example.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByUser(User user);

    @Modifying
    @Query(value = "INSERT INTO work_order (user_id, vehicle_id, problem, status, submit_time, progress) " +
            "VALUES (:userId, :vehicleId, :problem, :status, :submitTime, :progress)", nativeQuery = true)
    void insertWorkOrder(Long userId, Long vehicleId, String problem,
                         String status, LocalDateTime submitTime, Double progress);

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long getLastInsertedWorkOrderId();

    @Query(value = "SELECT * FROM work_order WHERE user_id = :userId", nativeQuery = true)
    List<WorkOrder> findByUserId(Long userId);

    @Query("SELECT new com.example.dto.VehicleTypeStatsDTO(v.vehicleType, COUNT(w), AVG(w.totalCost)) " +
            "FROM WorkOrder w JOIN Vehicle v ON w.vehicle = v GROUP BY v.vehicleType")
    List<VehicleTypeStatsDTO> findVehicleTypeStatistics();


    /* @Query("SELECT new com.example.dto.TechnicianStatsDTO(t.technicianId, t.jobType, COUNT(w), " +
            "CAST(COUNT(w) AS double) / NULLIF((SELECT COUNT(w2) FROM WorkOrder w2 WHERE w2.submitTime BETWEEN :startDate AND :endDate), 0)) " +
            "FROM Technician t LEFT JOIN t.workOrders w " +
            "WHERE (w.submitTime BETWEEN :startDate AND :endDate OR w IS NULL) GROUP BY t.technicianId, t.jobType")
    List<TechnicianStatsDTO> findTechnicianStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate); */



}
