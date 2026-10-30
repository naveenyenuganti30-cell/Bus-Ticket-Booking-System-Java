package com.seatreserve.busbooking.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.seatreserve.busbooking.model.enums.SeatStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journey_seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_journey_seat", columnNames = {"journey_schedule_id", "seat_id"})
})
public class JourneySeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_schedule_id", nullable = false)
    @JsonBackReference
    private JourneySchedule journeySchedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    private LocalDateTime heldUntil;

    @Column(nullable = false)
    private Double price;

    @Version
    private Long version;

    public JourneySeat() {}

    public JourneySeat(JourneySchedule journeySchedule, Seat seat, Double price) {
        this.journeySchedule = journeySchedule;
        this.seat = seat;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public JourneySchedule getJourneySchedule() { return journeySchedule; }
    public void setJourneySchedule(JourneySchedule journeySchedule) { this.journeySchedule = journeySchedule; }

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public LocalDateTime getHeldUntil() { return heldUntil; }
    public void setHeldUntil(LocalDateTime heldUntil) { this.heldUntil = heldUntil; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
