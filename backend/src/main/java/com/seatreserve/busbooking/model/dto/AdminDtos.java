package com.seatreserve.busbooking.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDtos {

    public static class AdminDashboardStatsDto {
        private long totalBookings;
        private long confirmedBookings;
        private long cancelledBookings;
        private Double totalRevenue;
        private Double totalRefunds;
        private Double netRevenue;
        private long totalRoutes;
        private long totalVehicles;
        private long upcomingJourneys;
        private double averageSeatOccupancyRate;
        private List<BookingDtos.BookingConfirmationResponse> recentBookings;

        public AdminDashboardStatsDto() {}

        public long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
        public long getConfirmedBookings() { return confirmedBookings; }
        public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }
        public long getCancelledBookings() { return cancelledBookings; }
        public void setCancelledBookings(long cancelledBookings) { this.cancelledBookings = cancelledBookings; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Double getTotalRefunds() { return totalRefunds; }
        public void setTotalRefunds(Double totalRefunds) { this.totalRefunds = totalRefunds; }
        public Double getNetRevenue() { return netRevenue; }
        public void setNetRevenue(Double netRevenue) { this.netRevenue = netRevenue; }
        public long getTotalRoutes() { return totalRoutes; }
        public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }
        public long getTotalVehicles() { return totalVehicles; }
        public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
        public long getUpcomingJourneys() { return upcomingJourneys; }
        public void setUpcomingJourneys(long upcomingJourneys) { this.upcomingJourneys = upcomingJourneys; }
        public double getAverageSeatOccupancyRate() { return averageSeatOccupancyRate; }
        public void setAverageSeatOccupancyRate(double averageSeatOccupancyRate) { this.averageSeatOccupancyRate = averageSeatOccupancyRate; }
        public List<BookingDtos.BookingConfirmationResponse> getRecentBookings() { return recentBookings; }
        public void setRecentBookings(List<BookingDtos.BookingConfirmationResponse> recentBookings) { this.recentBookings = recentBookings; }
    }

    public static class CreateRouteRequest {
        @NotBlank private String sourceCity;
        @NotBlank private String destinationCity;
        @NotNull private Double distanceKm;
        @NotNull private Double estimatedDurationHours;
        private List<JourneyDtos.RouteStopDto> stops;

        public String getSourceCity() { return sourceCity; }
        public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        public Double getEstimatedDurationHours() { return estimatedDurationHours; }
        public void setEstimatedDurationHours(Double estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
        public List<JourneyDtos.RouteStopDto> getStops() { return stops; }
        public void setStops(List<JourneyDtos.RouteStopDto> stops) { this.stops = stops; }
    }

    public static class CreateJourneyRequest {
        @NotNull private Long routeId;
        @NotNull private Long vehicleId;
        @NotNull private LocalDateTime departureDateTime;
        @NotNull private LocalDateTime arrivalDateTime;
        private Double baseRatePerKm;
        private Double serviceTaxPercentage;

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
        public LocalDateTime getDepartureDateTime() { return departureDateTime; }
        public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
        public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
        public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }
        public Double getBaseRatePerKm() { return baseRatePerKm; }
        public void setBaseRatePerKm(Double baseRatePerKm) { this.baseRatePerKm = baseRatePerKm; }
        public Double getServiceTaxPercentage() { return serviceTaxPercentage; }
        public void setServiceTaxPercentage(Double serviceTaxPercentage) { this.serviceTaxPercentage = serviceTaxPercentage; }
    }
}
