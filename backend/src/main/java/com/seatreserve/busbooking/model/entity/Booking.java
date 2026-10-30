package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.seatreserve.busbooking.model.enums.BookingStatus;
import com.seatreserve.busbooking.model.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_pnr", columnList = "booking_reference", unique = true)
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String passengerName;

    @Column(nullable = false)
    private String passengerEmail;

    @Column(nullable = false)
    private String passengerPhone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journey_schedule_id", nullable = false)
    private JourneySchedule journeySchedule;

    private String pickupStop;
    private String dropoffStop;

    @Column(nullable = false)
    private LocalDateTime bookingDateTime = LocalDateTime.now();

    @Column(nullable = false)
    private Double totalDistanceKm;

    @Column(nullable = false)
    private Double baseFare;

    @Column(nullable = false)
    private Double seatSurcharges = 0.0;

    @Column(nullable = false)
    private Double taxes;

    @Column(nullable = false)
    private Double discountAmount = 0.0;

    @Column(nullable = false)
    private Double totalFare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus = BookingStatus.CONFIRMED;

    private Double cancellationFee = 0.0;
    private Double refundAmount = 0.0;
    private LocalDateTime cancellationDateTime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BookingPassenger> passengers = new ArrayList<>();

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public String getPassengerPhone() { return passengerPhone; }
    public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }

    public JourneySchedule getJourneySchedule() { return journeySchedule; }
    public void setJourneySchedule(JourneySchedule journeySchedule) { this.journeySchedule = journeySchedule; }

    public String getPickupStop() { return pickupStop; }
    public void setPickupStop(String pickupStop) { this.pickupStop = pickupStop; }

    public String getDropoffStop() { return dropoffStop; }
    public void setDropoffStop(String dropoffStop) { this.dropoffStop = dropoffStop; }

    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public void setBookingDateTime(LocalDateTime bookingDateTime) { this.bookingDateTime = bookingDateTime; }

    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }

    public Double getSeatSurcharges() { return seatSurcharges; }
    public void setSeatSurcharges(Double seatSurcharges) { this.seatSurcharges = seatSurcharges; }

    public Double getTaxes() { return taxes; }
    public void setTaxes(Double taxes) { this.taxes = taxes; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getTotalFare() { return totalFare; }
    public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public Double getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(Double cancellationFee) { this.cancellationFee = cancellationFee; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public LocalDateTime getCancellationDateTime() { return cancellationDateTime; }
    public void setCancellationDateTime(LocalDateTime cancellationDateTime) { this.cancellationDateTime = cancellationDateTime; }

    public List<BookingPassenger> getPassengers() { return passengers; }
    public void setPassengers(List<BookingPassenger> passengers) { this.passengers = passengers; }

    public void addPassenger(BookingPassenger passenger) {
        passengers.add(passenger);
        passenger.setBooking(this);
    }
}
