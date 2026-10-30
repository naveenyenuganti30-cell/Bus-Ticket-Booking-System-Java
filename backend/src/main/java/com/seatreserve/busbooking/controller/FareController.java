package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.FareDtos;
import com.seatreserve.busbooking.service.FareService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<FareDtos.FareCalculationResponse> calculateFare(@Valid @RequestBody FareDtos.FareCalculationRequest request) {
        FareDtos.FareCalculationResponse response = fareService.calculateFare(request);
        return ResponseEntity.ok(response);
    }
}
