package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.seatreserve.busbooking.model.enums.BusType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private String busName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType busType;

    @Column(nullable = false)
    private Integer totalSeats;

    private Integer rowsCount;
    private Integer colsCount;
    private String layoutType; // e.g. "2+1_SLEEPER", "2+2_SEATER"

    private String amenities; // "WiFi,AC,Charging Point,Water Bottle,Reading Light,Blanket"

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("deck ASC, seatRow ASC, seatCol ASC")
    @JsonManagedReference
    private List<Seat> seats = new ArrayList<>();

    public Vehicle() {}

    public Vehicle(String registrationNumber, String busName, BusType busType, Integer totalSeats, 
                   Integer rowsCount, Integer colsCount, String layoutType, String amenities) {
        this.registrationNumber = registrationNumber;
        this.busName = busName;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
        this.layoutType = layoutType;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

    public BusType getBusType() { return busType; }
    public void setBusType(BusType busType) { this.busType = busType; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Integer getRowsCount() { return rowsCount; }
    public void setRowsCount(Integer rowsCount) { this.rowsCount = rowsCount; }

    public Integer getColsCount() { return colsCount; }
    public void setColsCount(Integer colsCount) { this.colsCount = colsCount; }

    public String getLayoutType() { return layoutType; }
    public void setLayoutType(String layoutType) { this.layoutType = layoutType; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setVehicle(this);
    }
}
