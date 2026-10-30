package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
}
