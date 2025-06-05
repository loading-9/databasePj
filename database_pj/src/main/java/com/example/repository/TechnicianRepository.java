package com.example.repository;

import com.example.entity.JobType;
import com.example.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByUsername(String username);

    boolean existsByUsername(String username);
    // @Query(value = "SELECT * FROM technician WHERE username = :username", nativeQuery = true)
    // Technician findByUsername(@Param("username") String username);


    @Query("SELECT t FROM Technician t WHERE t.jobType = :jobType AND t.technicianId > :technicianId")
    List<Technician> findByJobTypeAndTechnicianIdGreaterThan(@Param("jobType") JobType jobType, @Param("technicianId") Long technicianId);

    @Query("SELECT t FROM Technician t")
    List<Technician> findAllTechniciansSummary();

}
