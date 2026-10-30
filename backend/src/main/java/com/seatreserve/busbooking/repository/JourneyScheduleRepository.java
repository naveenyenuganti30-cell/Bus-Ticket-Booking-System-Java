package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.JourneySchedule;
import com.seatreserve.busbooking.model.enums.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JourneyScheduleRepository extends JpaRepository<JourneySchedule, Long> {

    @Query("SELECT js FROM JourneySchedule js " +
           "WHERE LOWER(js.route.sourceCity) = LOWER(:sourceCity) " +
           "AND LOWER(js.route.destinationCity) = LOWER(:destinationCity) " +
           "AND js.departureDateTime >= :startOfDay " +
           "AND js.departureDateTime <= :endOfDay " +
           "AND js.status != 'CANCELLED' " +
           "ORDER BY js.departureDateTime ASC")
    List<JourneySchedule> searchJourneys(
            @Param("sourceCity") String sourceCity,
            @Param("destinationCity") String destinationCity,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    List<JourneySchedule> findByDepartureDateTimeAfterAndStatusNotOrderByDepartureDateTimeAsc(
            LocalDateTime dateTime, JourneyStatus status);

    List<JourneySchedule> findAllByOrderByDepartureDateTimeDesc();

    @Query("SELECT COUNT(js) FROM JourneySchedule js WHERE js.departureDateTime >= :now AND js.status != 'CANCELLED'")
    long countUpcomingJourneys(@Param("now") LocalDateTime now);
}
