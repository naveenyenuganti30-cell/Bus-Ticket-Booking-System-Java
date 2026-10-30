package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.AdminDtos;
import com.seatreserve.busbooking.model.dto.BookingDtos;
import com.seatreserve.busbooking.model.dto.JourneyDtos;
import com.seatreserve.busbooking.model.entity.Booking;
import com.seatreserve.busbooking.model.entity.Vehicle;
import com.seatreserve.busbooking.model.enums.BookingStatus;
import com.seatreserve.busbooking.repository.BookingRepository;
import com.seatreserve.busbooking.repository.JourneyScheduleRepository;
import com.seatreserve.busbooking.repository.RouteRepository;
import com.seatreserve.busbooking.repository.VehicleRepository;
import com.seatreserve.busbooking.service.BookingService;
import com.seatreserve.busbooking.service.JourneyService;
import com.seatreserve.busbooking.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookingRepository bookingRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final JourneyScheduleRepository journeyScheduleRepository;
    private final BookingService bookingService;
    private final JourneyService journeyService;
    private final RouteService routeService;

    public AdminController(BookingRepository bookingRepository,
                           RouteRepository routeRepository,
                           VehicleRepository vehicleRepository,
                           JourneyScheduleRepository journeyScheduleRepository,
                           BookingService bookingService,
                           JourneyService journeyService,
                           RouteService routeService) {
        this.bookingRepository = bookingRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
        this.journeyScheduleRepository = journeyScheduleRepository;
        this.bookingService = bookingService;
        this.journeyService = journeyService;
        this.routeService = routeService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDtos.AdminDashboardStatsDto> getDashboardStats() {
        AdminDtos.AdminDashboardStatsDto stats = new AdminDtos.AdminDashboardStatsDto();

        long totalBookings = bookingRepository.count();
        long confirmed = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
        long cancelled = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);

        Double grossRevenue = bookingRepository.sumTotalFareByBookingStatus(BookingStatus.CONFIRMED);
        if (grossRevenue == null) grossRevenue = 0.0;

        Double refunds = bookingRepository.sumTotalRefunds();
        if (refunds == null) refunds = 0.0;

        Double netRevenue = Math.max(0.0, Math.round((grossRevenue - refunds) * 100.0) / 100.0);

        long routes = routeRepository.count();
        long vehicles = vehicleRepository.count();
        long upcomingJourneys = journeyScheduleRepository.countUpcomingJourneys(LocalDateTime.now());

        List<BookingDtos.BookingConfirmationResponse> recent = bookingRepository.findAllByOrderByBookingDateTimeDesc().stream()
                .limit(10)
                .map(bookingService::mapToBookingConfirmation)
                .collect(Collectors.toList());

        stats.setTotalBookings(totalBookings);
        stats.setConfirmedBookings(confirmed);
        stats.setCancelledBookings(cancelled);
        stats.setTotalRevenue(grossRevenue);
        stats.setTotalRefunds(refunds);
        stats.setNetRevenue(netRevenue);
        stats.setTotalRoutes(routes);
        stats.setTotalVehicles(vehicles);
        stats.setUpcomingJourneys(upcomingJourneys);
        stats.setAverageSeatOccupancyRate(totalBookings > 0 ? Math.min(85.5, Math.round((confirmed * 100.0 / Math.max(1, totalBookings)) * 10.0) / 10.0) : 0.0);
        stats.setRecentBookings(recent);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDtos.BookingConfirmationResponse>> getAllBookings(@RequestParam(required = false) String query) {
        List<Booking> bookings;
        if (query != null && !query.isBlank()) {
            bookings = bookingRepository.searchBookings(query.trim());
        } else {
            bookings = bookingRepository.findAllByOrderByBookingDateTimeDesc();
        }
        List<BookingDtos.BookingConfirmationResponse> list = bookings.stream()
                .map(bookingService::mapToBookingConfirmation)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAll());
    }

    @PostMapping("/routes")
    public ResponseEntity<JourneyDtos.RouteDto> createRoute(@Valid @RequestBody AdminDtos.CreateRouteRequest request) {
        return new ResponseEntity<>(routeService.createRoute(request), HttpStatus.CREATED);
    }

    @PostMapping("/journeys")
    public ResponseEntity<JourneyDtos.JourneyResponse> createJourney(@Valid @RequestBody AdminDtos.CreateJourneyRequest request) {
        return new ResponseEntity<>(journeyService.createJourney(request), HttpStatus.CREATED);
    }
}
