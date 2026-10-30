package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.AdminDtos;
import com.seatreserve.busbooking.model.dto.JourneyDtos;
import com.seatreserve.busbooking.model.entity.Route;
import com.seatreserve.busbooking.model.entity.RouteStop;
import com.seatreserve.busbooking.model.enums.StopType;
import com.seatreserve.busbooking.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<JourneyDtos.RouteDto> getAllActiveRoutes() {
        return routeRepository.findByIsActiveTrue().stream()
                .map(this::mapToRouteDto)
                .collect(Collectors.toList());
    }

    public JourneyDtos.RouteDto getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return mapToRouteDto(route);
    }

    public List<String> getDistinctSourceCities() {
        return routeRepository.findDistinctSourceCities();
    }

    public List<String> getDistinctDestinationCities() {
        return routeRepository.findDistinctDestinationCities();
    }

    @Transactional
    public JourneyDtos.RouteDto createRoute(AdminDtos.CreateRouteRequest request) {
        Route route = new Route(
                request.getSourceCity().trim(),
                request.getDestinationCity().trim(),
                request.getDistanceKm(),
                request.getEstimatedDurationHours()
        );

        if (request.getStops() != null) {
            for (JourneyDtos.RouteStopDto stopDto : request.getStops()) {
                RouteStop stop = new RouteStop(
                        stopDto.getStopName(),
                        stopDto.getStopOrder(),
                        stopDto.getStopType() != null ? StopType.valueOf(stopDto.getStopType()) : StopType.BOTH,
                        stopDto.getArrivalOffsetMinutes(),
                        stopDto.getLandmark()
                );
                route.addStop(stop);
            }
        }

        Route saved = routeRepository.save(route);
        return mapToRouteDto(saved);
    }

    public JourneyDtos.RouteDto mapToRouteDto(Route route) {
        List<JourneyDtos.RouteStopDto> stops = route.getStops().stream()
                .map(s -> new JourneyDtos.RouteStopDto(
                        s.getId(),
                        s.getStopName(),
                        s.getStopOrder(),
                        s.getStopType().name(),
                        s.getArrivalOffsetMinutes(),
                        s.getLandmark()
                ))
                .collect(Collectors.toList());

        return new JourneyDtos.RouteDto(
                route.getId(),
                route.getSourceCity(),
                route.getDestinationCity(),
                route.getDistanceKm(),
                route.getEstimatedDurationHours(),
                route.getIsActive(),
                stops
        );
    }
}
