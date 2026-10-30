package com.seatreserve.busbooking.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancellations")
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private LocalDateTime cancellationDateTime = LocalDateTime.now();

    private String reason;

    @Column(nullable = false)
    private Double originalFare;

    @Column(nullable = false)
    private Double cancellationFee;

    @Column(nullable = false)
    private Double refundAmount;

    @Column(nullable = false)
    private String refundStatus = "PROCESSED";

    @Column(nullable = false)
    private Integer cancelledSeatsCount;

    private String cancelledBy;

    public Cancellation() {}

    public Cancellation(Booking booking, String reason, Double originalFare, Double cancellationFee, 
                        Double refundAmount, Integer cancelledSeatsCount, String cancelledBy) {
        this.booking = booking;
        this.reason = reason;
        this.originalFare = originalFare;
        this.cancellationFee = cancellationFee;
        this.refundAmount = refundAmount;
        this.cancelledSeatsCount = cancelledSeatsCount;
        this.cancelledBy = cancelledBy;
        this.cancellationDateTime = LocalDateTime.now();
        this.refundStatus = "PROCESSED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public LocalDateTime getCancellationDateTime() { return cancellationDateTime; }
    public void setCancellationDateTime(LocalDateTime cancellationDateTime) { this.cancellationDateTime = cancellationDateTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getOriginalFare() { return originalFare; }
    public void setOriginalFare(Double originalFare) { this.originalFare = originalFare; }

    public Double getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(Double cancellationFee) { this.cancellationFee = cancellationFee; }

    public Double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public Integer getCancelledSeatsCount() { return cancelledSeatsCount; }
    public void setCancelledSeatsCount(Integer cancelledSeatsCount) { this.cancelledSeatsCount = cancelledSeatsCount; }

    public String getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(String cancelledBy) { this.cancelledBy = cancelledBy; }
}
