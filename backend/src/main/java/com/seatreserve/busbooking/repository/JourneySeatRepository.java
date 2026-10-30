package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.JourneySeat;
import com.seatreserve.busbooking.model.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JourneySeatRepository extends JpaRepository<JourneySeat, Long> {

    List<JourneySeat> findByJourneyScheduleIdOrderBySeat_DeckAscSeat_SeatRowAscSeat_SeatColAsc(Long journeyScheduleId);

    Optional<JourneySeat> findByJourneyScheduleIdAndSeatId(Long journeyScheduleId, Long seatId);

    long countByJourneyScheduleIdAndStatus(Long journeyScheduleId, SeatStatus status);

    @Query("SELECT COUNT(js) FROM JourneySeat js WHERE js.journeySchedule.id = :journeyScheduleId")
    long countTotalSeats(@Param("journeyScheduleId") Long journeyScheduleId);

    /**
     * CRITICAL: Pessimistic write lock on JourneySeat rows during booking transaction
     * to prevent any concurrent double-booking of identical seats.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT js FROM JourneySeat js WHERE js.id IN :seatIds")
    List<JourneySeat> findSeatsWithPessimisticLock(@Param("seatIds") List<Long> seatIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT js FROM JourneySeat js WHERE js.journeySchedule.id = :journeyId AND js.seat.seatNumber IN :seatNumbers")
    List<JourneySeat> findByJourneyIdAndSeatNumbersWithLock(
            @Param("journeyId") Long journeyId, 
            @Param("seatNumbers") List<String> seatNumbers);
}
