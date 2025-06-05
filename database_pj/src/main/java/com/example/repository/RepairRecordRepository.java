
package com.example.repository;

import com.example.dto.RepairRecordDTO;
import com.example.entity.RepairRecord;
import com.example.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RepairRecordRepository extends JpaRepository<RepairRecord, Long> {

    @Query("""
        SELECT new com.example.dto.RepairRecordDTO(r, r.workOrder, r.technician, r.workOrder.user)
        FROM RepairRecord r
        JOIN Technician t ON r.technician.technicianId = t.technicianId
    """)
    List<RepairRecordDTO> findAllRepairHistories();

    RepairRecord findByWorkOrder(WorkOrder workOrder);
}

