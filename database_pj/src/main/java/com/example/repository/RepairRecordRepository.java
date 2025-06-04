package com.example.repository;

import com.example.entity.RepairRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairRecordRepository extends JpaRepository<RepairRecord, Long> {
}
