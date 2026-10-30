package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.AdminDtos;
import com.seatreserve.busbooking.model.dto.JourneyDtos;
import com.seatreserve.busbooking.model.dto.SeatDtos;
import com.seatreserve.busbooking.model.entity.*;
import com.seatreserve.busbooking.model.enums.Deck;
import com.seatreserve.busbooking.model.enums.SeatStatus;
import com.seatreserve.busbooking.model.enums.StopType;
import com.seatreserve.busbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JourneyService {

    private final JourneyScheduleRepository journeyScheduleRepository;
    private final JourneySeatRepository journeySeatRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;

    public JourneyService(JourneyScheduleRepository journeyScheduleRepository,
                          JourneySeatRepository journeySeatRepository,
                          RouteRepository routeRepository,
                          VehicleRepository vehicleRepository) {
        this.journeyScheduleRepository = journeyScheduleRepository;
        this.journeySeatRepository = journeySeatRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<JourneyDtos.JourneyResponse> searchJourneys(String sourceCity, String destinationCity, LocalDate travelDate) {
        LocalDateTime startOfDay = travelDate.atStartOfDay();
        LocalDateTime endOfDay = travelDate.atTime(LocalTime.MAX);

        List<JourneySchedule> journeys = journeyScheduleRepository.searchJourneys(
                sourceCity.trim(), destinationCity.trim(), startOfDay, endOfDay
        );

        return journeys.stream()
                .map(this::mapToJourneyResponse)
                .collect(Collectors.toList());
    }

    public JourneyDtos.JourneyResponse getJourneyById(Long id) {
        JourneySchedule journey = journeyScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + id));
        return mapToJourneyResponse(journey);
    }

    public SeatDtos.SeatLayoutResponse getSeatLayout(Long journeyId) {
        JourneySchedule journey = journeyScheduleRepository.findById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + journeyId));

        List<JourneySeat> journeySeats = journeySeatRepository.findByJourneyScheduleIdOrderBySeat_DeckAscSeat_SeatRowAscSeat_SeatColAsc(journeyId);

        long availableSeats = journeySeats.stream().filter(js -> js.getStatus() == SeatStatus.AVAILABLE).count();
        long bookedSeats = journeySeats.stream().filter(js -> js.getStatus() == SeatStatus.BOOKED).count();

        List<SeatDtos.SeatDto> lowerDeck = new ArrayList<>();
        List<SeatDtos.SeatDto> upperDeck = new ArrayList<>();

        for (JourneySeat js : journeySeats) {
            Seat seat = js.getSeat();
            SeatDtos.SeatDto dto = new SeatDtos.SeatDto();
            dto.setJourneySeatId(js.getId());
            dto.setSeatId(seat.getId());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setDeck(seat.getDeck().name());
            dto.setSeatType(seat.getSeatType().name());
            dto.setPosition(seat.getPosition().name());
            dto.setSeatRow(seat.getSeatRow());
            dto.setSeatCol(seat.getSeatCol());
            dto.setStatus(js.getStatus().name());
            dto.setPrice(js.getPrice());
            dto.setSurchargePercentage(seat.getSurchargePercentage());

            if (seat.getDeck() == Deck.UPPER) {
                upperDeck.add(dto);
            } else {
                lowerDeck.add(dto);
            }
        }

        SeatDtos.SeatLayoutResponse response = new SeatDtos.SeatLayoutResponse();
        response.setJourneyId(journey.getId());
        response.setBusName(journey.getVehicle().getBusName());
        response.setBusType(journey.getVehicle().getBusType().name());
        response.setBusTypeDisplay(journey.getVehicle().getBusType().getDisplayName());
        response.setLayoutType(journey.getVehicle().getLayoutType());
        response.setTotalSeats(journeySeats.size());
        response.setAvailableSeats(availableSeats);
        response.setBookedSeats(bookedSeats);
        response.setBaseFare(journey.getRoute().getDistanceKm() * journey.getBaseRatePerKm());
        response.setLowerDeck(lowerDeck);
        response.setUpperDeck(upperDeck);

        return response;
    }

    @Transactional
    public JourneyDtos.JourneyResponse createJourney(AdminDtos.CreateJourneyRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + request.getRouteId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

        double baseRate = request.getBaseRatePerKm() != null ? request.getBaseRatePerKm() : 1.85;
        double tax = request.getServiceTaxPercentage() != null ? request.getServiceTaxPercentage() : 5.0;

        JourneySchedule schedule = new JourneySchedule(
                route, vehicle, request.getDepartureDateTime(), request.getArrivalDateTime(), baseRate, tax
        );

        schedule = journeyScheduleRepository.save(schedule);

        // Populate JourneySeats from vehicle seats
        double baseFare = route.getDistanceKm() * baseRate;
        for (Seat seat : vehicle.getSeats()) {
            double seatSurcharge = baseFare * (seat.getSurchargePercentage() / 100.0);
            double seatPrice = Math.round((baseFare + seatSurcharge) * 100.0) / 100.0;
            JourneySeat js = new JourneySeat(schedule, seat, seatPrice);
            journeySeatRepository.save(js);
        }

        return mapToJourneyResponse(schedule);
    }

    public JourneyDtos.JourneyResponse mapToJourneyResponse(JourneySchedule journey) {
        Route route = journey.getRoute();
        Vehicle vehicle = journey.getVehicle();

        long availableSeats = journeySeatRepository.countByJourneyScheduleIdAndStatus(journey.getId(), SeatStatus.AVAILABLE);
        long totalSeats = journeySeatRepository.countTotalSeats(journey.getId());

        List<String> amenities = vehicle.getAmenities() != null
                ? Arrays.stream(vehicle.getAmenities().split(",")).map(String::trim).collect(Collectors.toList())
                : Collections.emptyList();

        List<JourneyDtos.RouteStopDto> boardingPoints = route.getStops().stream()
                .filter(s -> s.getStopType() == StopType.PICKUP || s.getStopType() == StopType.BOTH)
                .map(s -> new JourneyDtos.RouteStopDto(s.getId(), s.getStopName(), s.getStopOrder(), s.getStopType().name(), s.getArrivalOffsetMinutes(), s.getLandmark()))
                .collect(Collectors.toList());

        List<JourneyDtos.RouteStopDto> dropoffPoints = route.getStops().stream()
                .filter(s -> s.getStopType() == StopType.DROPOFF || s.getStopType() == StopType.BOTH)
                .map(s -> new JourneyDtos.RouteStopDto(s.getId(), s.getStopName(), s.getStopOrder(), s.getStopType().name(), s.getArrivalOffsetMinutes(), s.getLandmark()))
                .collect(Collectors.toList());

        JourneyDtos.JourneyResponse res = new JourneyDtos.JourneyResponse();
        res.setId(journey.getId());
        res.setRouteId(route.getId());
        res.setSourceCity(route.getSourceCity());
        res.setDestinationCity(route.getDestinationCity());
        res.setDistanceKm(route.getDistanceKm());
        res.setBusName(vehicle.getBusName());
        res.setBusType(vehicle.getBusType().name());
        res.setBusTypeDisplay(vehicle.getBusType().getDisplayName());
        res.setRegistrationNumber(vehicle.getRegistrationNumber());
        res.setAmenities(amenities);
        res.setDepartureDateTime(journey.getDepartureDateTime());
        res.setArrivalDateTime(journey.getArrivalDateTime());
        res.setDurationHours(route.getEstimatedDurationHours());
        res.setBaseFare(Math.round((route.getDistanceKm() * journey.getBaseRatePerKm()) * 100.0) / 100.0);
        res.setAvailableSeats(availableSeats);
        res.setTotalSeats(totalSeats);
        res.setBoardingPoints(boardingPoints);
        res.setDropoffPoints(dropoffPoints);

        return res;
    }
}
