package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVehicleIdOrderByDeckAscSeatRowAscSeatColAsc(Long vehicleId);
}
