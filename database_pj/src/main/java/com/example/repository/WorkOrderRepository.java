package com.example.repository;


import com.example.dto.AdvancedStats.*;
import com.example.entity.Technician;
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

    @Modifying
    @Query(value = "INSERT INTO work_order (user_id, vehicle_id, problem, status, submit_time, progress) " +
            "VALUES (:userId, :vehicleId, :problem, :status, :submitTime, :progress)", nativeQuery = true)
    void insertWorkOrder(Long userId, Long vehicleId, String problem,
                         String status, LocalDateTime submitTime, Double progress);

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long getLastInsertedWorkOrderId();

    @Query(value = "SELECT * FROM work_order WHERE user_id = :userId", nativeQuery = true)
    List<WorkOrder> findByUserId(Long userId);

    @Query("SELECT w FROM WorkOrder w JOIN FETCH w.vehicle v JOIN FETCH w.technician t")
    List<WorkOrder> findAllWorkOrders();


    List<WorkOrder> findByTechnician(Technician technician);

    /* @Query("""
        SELECT new com.example.dto.AdvancedStats.VehicleRepairStatsDTO(
            v.vehicleType,
            COUNT(w),
            AVG(w.totalCost),
            (SELECT w2.problem
             FROM WorkOrder w2
             WHERE w2.vehicle.vehicleType = v.vehicleType
             GROUP BY w2.problem
             ORDER BY COUNT(w2.problem) DESC
             LIMIT 1)
        )
        FROM WorkOrder w
        JOIN w.vehicle v
        GROUP BY v.vehicleType
    """)
    List<VehicleRepairStatsDTO> findVehicleRepairStats(); */

    /* @Query("""
        SELECT new com.example.dto.AdvancedStats.VehicleRepairStatsDTO(
            v.vehicleType,
            COUNT(w),
            AVG(w.totalCost),
            (SELECT w2.problem
             FROM WorkOrder w2
             WHERE w2.vehicle.vehicleType = v.vehicleType
             GROUP BY w2.problem
             ORDER BY COUNT(w2.problem) DESC
             LIMIT 1)
        )
        FROM WorkOrder w
        JOIN w.vehicle v
        WHERE v.vehicleType = :vehicleType
        GROUP BY v.vehicleType
    """)
    VehicleRepairStatsDTO findVehicleRepairStatsByType(@Param("vehicleType") String vehicleType); */

    /* @Query("""
    SELECT new com.example.dto.AdvancedStats.CostAnalysisDTO(
        CONCAT(YEAR(w.completeTime), '-', MONTH(w.completeTime)),
        SUM(w.laborCost),
        SUM(w.materialCost),
        SUM(w.totalCost),
        SUM(w.laborCost) / SUM(w.totalCost) * 100,
        SUM(w.materialCost) / SUM(w.totalCost) * 100
    )
    FROM WorkOrder w
    WHERE w.completeTime IS NOT NULL
    AND w.completeTime BETWEEN :startTime AND :endTime
    GROUP BY YEAR(w.completeTime), MONTH(w.completeTime)
""")
    List<CostAnalysisDTO> findCostAnalysisByMonth(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime); */

    @Query("""
        SELECT new com.example.dto.AdvancedStats.NegativeFeedbackDTO(
            w.workOrderId,
            f.feedbackId,
            f.rating,
            f.comment,
            t.technicianId,
            t.name
        )
        FROM WorkOrder w
        JOIN w.feedback f
        JOIN w.technician t
        WHERE f.rating <= 3
    """)
    List<NegativeFeedbackDTO> findNegativeFeedbackWorkOrders();

    /* @Query("""
        SELECT new com.example.dto.AdvancedStats.TaskStatsDTO(
            t.jobType,
            COUNT(w),
            (SELECT COUNT(w2) FROM WorkOrder w2 WHERE w2.completeTime IS NOT NULL),
            COUNT(w) * 100.0 / (SELECT COUNT(w2) FROM WorkOrder w2 WHERE w2.completeTime IS NOT NULL)
        )
        FROM WorkOrder w
        JOIN w.technician t
        WHERE w.completeTime IS NOT NULL
        GROUP BY t.jobType
    """)
    List<TaskStatsDTO> findTaskStatsByJobType(); */

    @Query("""
        SELECT new com.example.dto.AdvancedStats.PendingWorkOrderDTO(
            w.workOrderId,
            v.vehicleType,
            w.problem,
            t.jobType,
            t.technicianId,
            t.name
        )
        FROM WorkOrder w
        JOIN w.vehicle v
        LEFT JOIN w.technician t
        WHERE w.status != '已完成'
    """)
    List<PendingWorkOrderDTO> findPendingWorkOrders();
}
