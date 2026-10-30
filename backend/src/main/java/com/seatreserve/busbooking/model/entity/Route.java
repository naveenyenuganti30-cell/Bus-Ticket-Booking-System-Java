package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceCity;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Double estimatedDurationHours;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stopOrder ASC")
    @JsonManagedReference
    private List<RouteStop> stops = new ArrayList<>();

    public Route() {}

    public Route(String sourceCity, String destinationCity, Double distanceKm, Double estimatedDurationHours) {
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.distanceKm = distanceKm;
        this.estimatedDurationHours = estimatedDurationHours;
        this.isActive = true;
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

    public List<RouteStop> getStops() { return stops; }
    public void setStops(List<RouteStop> stops) { this.stops = stops; }

    public void addStop(RouteStop stop) {
        stops.add(stop);
        stop.setRoute(this);
    }
}
