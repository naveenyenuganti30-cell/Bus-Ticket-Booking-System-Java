package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.seatreserve.busbooking.model.enums.JourneyStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journey_schedules")
public class JourneySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDateTime departureDateTime;

    @Column(nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(nullable = false)
    private Double baseRatePerKm = 1.85;

    @Column(nullable = false)
    private Double serviceTaxPercentage = 5.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourneyStatus status = JourneyStatus.SCHEDULED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "journeySchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<JourneySeat> journeySeats = new ArrayList<>();

    public JourneySchedule() {}

    public JourneySchedule(Route route, Vehicle vehicle, LocalDateTime departureDateTime, 
                           LocalDateTime arrivalDateTime, Double baseRatePerKm, Double serviceTaxPercentage) {
        this.route = route;
        this.vehicle = vehicle;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.baseRatePerKm = baseRatePerKm != null ? baseRatePerKm : 1.85;
        this.serviceTaxPercentage = serviceTaxPercentage != null ? serviceTaxPercentage : 5.0;
        this.status = JourneyStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }

    public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
    public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }

    public Double getBaseRatePerKm() { return baseRatePerKm; }
    public void setBaseRatePerKm(Double baseRatePerKm) { this.baseRatePerKm = baseRatePerKm; }

    public Double getServiceTaxPercentage() { return serviceTaxPercentage; }
    public void setServiceTaxPercentage(Double serviceTaxPercentage) { this.serviceTaxPercentage = serviceTaxPercentage; }

    public JourneyStatus getStatus() { return status; }
    public void setStatus(JourneyStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<JourneySeat> getJourneySeats() { return journeySeats; }
    public void setJourneySeats(List<JourneySeat> journeySeats) { this.journeySeats = journeySeats; }

    public void addJourneySeat(JourneySeat journeySeat) {
        this.journeySeats.add(journeySeat);
        journeySeat.setJourneySchedule(this);
    }
}
