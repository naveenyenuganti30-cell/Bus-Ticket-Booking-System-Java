package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_passengers", indexes = {
    @Index(name = "idx_ticket_number", columnList = "ticket_number", unique = true)
})
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journey_seat_id", nullable = false)
    private JourneySeat journeySeat;

    @Column(nullable = false)
    private String passengerName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private Double individualFare;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String ticketStatus = "CONFIRMED";

    public BookingPassenger() {}

    public BookingPassenger(String passengerName, Integer age, String gender, String seatNumber, 
                            Double individualFare, String ticketNumber, JourneySeat journeySeat) {
        this.passengerName = passengerName;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
        this.individualFare = individualFare;
        this.ticketNumber = ticketNumber;
        this.journeySeat = journeySeat;
        this.ticketStatus = "CONFIRMED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public JourneySeat getJourneySeat() { return journeySeat; }
    public void setJourneySeat(JourneySeat journeySeat) { this.journeySeat = journeySeat; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Double getIndividualFare() { return individualFare; }
    public void setIndividualFare(Double individualFare) { this.individualFare = individualFare; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }
}
