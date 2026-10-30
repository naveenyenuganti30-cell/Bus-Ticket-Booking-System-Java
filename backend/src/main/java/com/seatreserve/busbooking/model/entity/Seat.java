package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.seatreserve.busbooking.model.enums.Deck;
import com.seatreserve.busbooking.model.enums.SeatPosition;
import com.seatreserve.busbooking.model.enums.SeatType;
import jakarta.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vehicle_seat_number", columnNames = {"vehicle_id", "seat_number"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonBackReference
    private Vehicle vehicle;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Deck deck = Deck.LOWER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType = SeatType.SEATER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatPosition position = SeatPosition.WINDOW;

    private Integer seatRow;
    private Integer seatCol;

    @Column(nullable = false)
    private Double surchargePercentage = 0.0;

    public Seat() {}

    public Seat(String seatNumber, Deck deck, SeatType seatType, SeatPosition position, 
                Integer seatRow, Integer seatCol, Double surchargePercentage) {
        this.seatNumber = seatNumber;
        this.deck = deck;
        this.seatType = seatType;
        this.position = position;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.surchargePercentage = surchargePercentage != null ? surchargePercentage : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Deck getDeck() { return deck; }
    public void setDeck(Deck deck) { this.deck = deck; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }

    public SeatPosition getPosition() { return position; }
    public void setPosition(SeatPosition position) { this.position = position; }

    public Integer getSeatRow() { return seatRow; }
    public void setSeatRow(Integer seatRow) { this.seatRow = seatRow; }

    public Integer getSeatCol() { return seatCol; }
    public void setSeatCol(Integer seatCol) { this.seatCol = seatCol; }

    public Double getSurchargePercentage() { return surchargePercentage; }
    public void setSurchargePercentage(Double surchargePercentage) { this.surchargePercentage = surchargePercentage; }
}
