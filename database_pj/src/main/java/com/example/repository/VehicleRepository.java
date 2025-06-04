package com.example.repository;

import com.example.entity.User;
import com.example.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Modifying
    @Query(value = """
    INSERT INTO vehicle (user_id, license_plate, vehicle_type, brand, manufacture_year)
    VALUES (:userId, :licensePlate, :vehicleType, :brand, :manufactureYear)
""", nativeQuery = true)
    void insertVehicle(
            @Param("userId") Long userId,
            @Param("licensePlate") String licensePlate,
            @Param("vehicleType") String vehicleType,
            @Param("brand") String brand,
            @Param("manufactureYear") Integer manufactureYear
    );

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long getLastInsertedVehicleId();
}
