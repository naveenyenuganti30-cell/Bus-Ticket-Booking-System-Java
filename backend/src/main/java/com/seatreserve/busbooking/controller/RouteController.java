package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.AdminDtos;
import com.seatreserve.busbooking.model.dto.JourneyDtos;
import com.seatreserve.busbooking.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<JourneyDtos.RouteDto>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllActiveRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JourneyDtos.RouteDto> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/cities/source")
    public ResponseEntity<List<String>> getDistinctSourceCities() {
        return ResponseEntity.ok(routeService.getDistinctSourceCities());
    }

    @GetMapping("/cities/destination")
    public ResponseEntity<List<String>> getDistinctDestinationCities() {
        return ResponseEntity.ok(routeService.getDistinctDestinationCities());
    }

    @PostMapping
    public ResponseEntity<JourneyDtos.RouteDto> createRoute(@Valid @RequestBody AdminDtos.CreateRouteRequest request) {
        JourneyDtos.RouteDto created = routeService.createRoute(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
