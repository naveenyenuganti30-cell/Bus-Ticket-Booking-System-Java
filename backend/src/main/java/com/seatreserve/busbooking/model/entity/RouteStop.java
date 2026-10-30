package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.seatreserve.busbooking.model.enums.StopType;
import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonBackReference
    private Route route;

    @Column(nullable = false)
    private String stopName;

    @Column(nullable = false)
    private Integer stopOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StopType stopType = StopType.BOTH;

    private Integer arrivalOffsetMinutes;

    private String landmark;

    public RouteStop() {}

    public RouteStop(String stopName, Integer stopOrder, StopType stopType, Integer arrivalOffsetMinutes, String landmark) {
        this.stopName = stopName;
        this.stopOrder = stopOrder;
        this.stopType = stopType;
        this.arrivalOffsetMinutes = arrivalOffsetMinutes;
        this.landmark = landmark;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }

    public StopType getStopType() { return stopType; }
    public void setStopType(StopType stopType) { this.stopType = stopType; }

    public Integer getArrivalOffsetMinutes() { return arrivalOffsetMinutes; }
    public void setArrivalOffsetMinutes(Integer arrivalOffsetMinutes) { this.arrivalOffsetMinutes = arrivalOffsetMinutes; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }
}
