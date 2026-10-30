package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.AdminDtos;
import com.seatreserve.busbooking.model.dto.JourneyDtos;
import com.seatreserve.busbooking.model.dto.SeatDtos;
import com.seatreserve.busbooking.service.JourneyService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<JourneyDtos.JourneyResponse>> searchJourneys(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<JourneyDtos.JourneyResponse> results = journeyService.searchJourneys(source, destination, date);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JourneyDtos.JourneyResponse> getJourneyById(@PathVariable Long id) {
        return ResponseEntity.ok(journeyService.getJourneyById(id));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<SeatDtos.SeatLayoutResponse> getSeatLayout(@PathVariable Long id) {
        return ResponseEntity.ok(journeyService.getSeatLayout(id));
    }

    @PostMapping
    public ResponseEntity<JourneyDtos.JourneyResponse> createJourney(@Valid @RequestBody AdminDtos.CreateJourneyRequest request) {
        JourneyDtos.JourneyResponse response = journeyService.createJourney(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
