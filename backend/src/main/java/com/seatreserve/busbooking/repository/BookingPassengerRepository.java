package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.BookingPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    Optional<BookingPassenger> findByTicketNumber(String ticketNumber);
    List<BookingPassenger> findByBookingId(Long bookingId);
}
