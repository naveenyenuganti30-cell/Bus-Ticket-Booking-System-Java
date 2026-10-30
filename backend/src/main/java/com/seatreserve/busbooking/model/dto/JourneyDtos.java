package com.seatreserve.busbooking.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JourneyDtos {

    public static class RouteStopDto {
        private Long id;
        private String stopName;
        private Integer stopOrder;
        private String stopType;
        private Integer arrivalOffsetMinutes;
        private String landmark;

        public RouteStopDto() {}

        public RouteStopDto(Long id, String stopName, Integer stopOrder, String stopType, Integer arrivalOffsetMinutes, String landmark) {
            this.id = id;
            this.stopName = stopName;
            this.stopOrder = stopOrder;
            this.stopType = stopType;
            this.arrivalOffsetMinutes = arrivalOffsetMinutes;
            this.landmark = landmark;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        public Integer getStopOrder() { return stopOrder; }
        public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }
        public String getStopType() { return stopType; }
        public void setStopType(String stopType) { this.stopType = stopType; }
        public Integer getArrivalOffsetMinutes() { return arrivalOffsetMinutes; }
        public void setArrivalOffsetMinutes(Integer arrivalOffsetMinutes) { this.arrivalOffsetMinutes = arrivalOffsetMinutes; }
        public String getLandmark() { return landmark; }
        public void setLandmark(String landmark) { this.landmark = landmark; }
    }

    public static class RouteDto {
        private Long id;
        private String sourceCity;
        private String destinationCity;
        private Double distanceKm;
        private Double estimatedDurationHours;
        private Boolean isActive;
        private List<RouteStopDto> stops;

        public RouteDto() {}

        public RouteDto(Long id, String sourceCity, String destinationCity, Double distanceKm, Double estimatedDurationHours, Boolean isActive, List<RouteStopDto> stops) {
            this.id = id;
            this.sourceCity = sourceCity;
            this.destinationCity = destinationCity;
            this.distanceKm = distanceKm;
            this.estimatedDurationHours = estimatedDurationHours;
            this.isActive = isActive;
            this.stops = stops;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSourceCity() { return sourceCity; }
        public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        public Double getEstimatedDurationHours() { return estimatedDurationHours; }
        public void setEstimatedDurationHours(Double estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public List<RouteStopDto> getStops() { return stops; }
        public void setStops(List<RouteStopDto> stops) { this.stops = stops; }
    }

    public static class JourneyResponse {
        private Long id;
        private Long routeId;
        private String sourceCity;
        private String destinationCity;
        private Double distanceKm;
        private String busName;
        private String busType;
        private String busTypeDisplay;
        private String registrationNumber;
        private List<String> amenities;
        private LocalDateTime departureDateTime;
        private LocalDateTime arrivalDateTime;
        private Double durationHours;
        private Double baseFare;
        private long availableSeats;
        private long totalSeats;
        private List<RouteStopDto> boardingPoints;
        private List<RouteStopDto> dropoffPoints;

        public JourneyResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public String getSourceCity() { return sourceCity; }
        public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public String getBusType() { return busType; }
        public void setBusType(String busType) { this.busType = busType; }
        public String getBusTypeDisplay() { return busTypeDisplay; }
        public void setBusTypeDisplay(String busTypeDisplay) { this.busTypeDisplay = busTypeDisplay; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public List<String> getAmenities() { return amenities; }
        public void setAmenities(List<String> amenities) { this.amenities = amenities; }
        public LocalDateTime getDepartureDateTime() { return departureDateTime; }
        public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
        public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
        public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }
        public Double getDurationHours() { return durationHours; }
        public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
        public Double getBaseFare() { return baseFare; }
        public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }
        public long getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(long availableSeats) { this.availableSeats = availableSeats; }
        public long getTotalSeats() { return totalSeats; }
        public void setTotalSeats(long totalSeats) { this.totalSeats = totalSeats; }
        public List<RouteStopDto> getBoardingPoints() { return boardingPoints; }
        public void setBoardingPoints(List<RouteStopDto> boardingPoints) { this.boardingPoints = boardingPoints; }
        public List<RouteStopDto> getDropoffPoints() { return dropoffPoints; }
        public void setDropoffPoints(List<RouteStopDto> dropoffPoints) { this.dropoffPoints = dropoffPoints; }
    }
}
