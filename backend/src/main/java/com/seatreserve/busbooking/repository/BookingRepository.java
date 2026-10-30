package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.Booking;
import com.seatreserve.busbooking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Optional<Booking> findByBookingReferenceAndPassengerEmailIgnoreCase(String bookingReference, String passengerEmail);

    List<Booking> findByUserIdOrderByBookingDateTimeDesc(Long userId);

    List<Booking> findByPassengerEmailIgnoreCaseOrderByBookingDateTimeDesc(String email);

    List<Booking> findAllByOrderByBookingDateTimeDesc();

    long countByBookingStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalFare), 0.0) FROM Booking b WHERE b.bookingStatus = :status")
    Double sumTotalFareByBookingStatus(@Param("status") BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.refundAmount), 0.0) FROM Booking b WHERE b.bookingStatus = 'CANCELLED' OR b.bookingStatus = 'PARTIALLY_CANCELLED'")
    Double sumTotalRefunds();

    @Query("SELECT b FROM Booking b WHERE LOWER(b.bookingReference) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(b.passengerName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(b.passengerEmail) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY b.bookingDateTime DESC")
    List<Booking> searchBookings(@Param("query") String query);
}
