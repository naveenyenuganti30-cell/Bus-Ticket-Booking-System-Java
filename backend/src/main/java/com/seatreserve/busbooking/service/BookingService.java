package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.exception.DoubleBookingConflictException;
import com.seatreserve.busbooking.exception.InvalidBookingException;
import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.BookingDtos;
import com.seatreserve.busbooking.model.dto.FareDtos;
import com.seatreserve.busbooking.model.entity.*;
import com.seatreserve.busbooking.model.enums.BookingStatus;
import com.seatreserve.busbooking.model.enums.PaymentStatus;
import com.seatreserve.busbooking.model.enums.SeatStatus;
import com.seatreserve.busbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final JourneyScheduleRepository journeyScheduleRepository;
    private final JourneySeatRepository journeySeatRepository;
    private final UserRepository userRepository;
    private final CancellationRepository cancellationRepository;
    private final AuditLogRepository auditLogRepository;
    private final FareService fareService;

    public BookingService(BookingRepository bookingRepository,
                          JourneyScheduleRepository journeyScheduleRepository,
                          JourneySeatRepository journeySeatRepository,
                          UserRepository userRepository,
                          CancellationRepository cancellationRepository,
                          AuditLogRepository auditLogRepository,
                          FareService fareService) {
        this.bookingRepository = bookingRepository;
        this.journeyScheduleRepository = journeyScheduleRepository;
        this.journeySeatRepository = journeySeatRepository;
        this.userRepository = userRepository;
        this.cancellationRepository = cancellationRepository;
        this.auditLogRepository = auditLogRepository;
        this.fareService = fareService;
    }

    /**
     * CRITICAL: Atomic booking creation with Pessimistic Write Locking.
     * Prevents race conditions and double bookings when concurrent requests target the same seat.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDtos.BookingConfirmationResponse createBooking(BookingDtos.CreateBookingRequest request, String currentUserEmail) {
        JourneySchedule journey = journeyScheduleRepository.findById(request.getJourneyScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + request.getJourneyScheduleId()));

        if (journey.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingException("Cannot book seats for a journey that has already departed.");
        }

        List<Long> seatIds = request.getPassengers().stream()
                .map(BookingDtos.PassengerDetailDto::getJourneySeatId)
                .collect(Collectors.toList());

        // Check for duplicates inside the same request
        Set<Long> uniqueIds = new HashSet<>(seatIds);
        if (uniqueIds.size() != seatIds.size()) {
            throw new InvalidBookingException("Duplicate seats specified in the booking request.");
        }

        // ACQUIRE PESSIMISTIC EXCLUSIVE LOCKS ON REQUESTED SEATS
        List<JourneySeat> lockedSeats = journeySeatRepository.findSeatsWithPessimisticLock(seatIds);
        if (lockedSeats.size() != seatIds.size()) {
            throw new ResourceNotFoundException("One or more selected seats could not be found.");
        }

        // STRICT CONCURRENCY VALIDATION: Check each seat status
        for (JourneySeat js : lockedSeats) {
            if (js.getStatus() != SeatStatus.AVAILABLE) {
                throw new DoubleBookingConflictException(
                        "Seat " + js.getSeat().getSeatNumber() + " is already " + js.getStatus().name().toLowerCase() +
                        " by another passenger. Please select another seat."
                );
            }
        }

        // Calculate authoritative backend fare
        FareDtos.FareCalculationRequest fareReq = new FareDtos.FareCalculationRequest();
        fareReq.setJourneyId(journey.getId());
        fareReq.setSeatIds(seatIds);
        fareReq.setPromoCode(request.getPromoCode());
        FareDtos.FareCalculationResponse fareQuote = fareService.calculateFare(fareReq);

        // Generate Unique PNR
        String pnr = generateUniquePnr();

        // Optional User association
        User user = null;
        if (currentUserEmail != null && !currentUserEmail.isBlank()) {
            user = userRepository.findByEmail(currentUserEmail).orElse(null);
        } else if (request.getPassengerEmail() != null) {
            user = userRepository.findByEmail(request.getPassengerEmail()).orElse(null);
        }

        Booking booking = new Booking();
        booking.setBookingReference(pnr);
        booking.setUser(user);
        booking.setPassengerName(request.getPassengerName().trim());
        booking.setPassengerEmail(request.getPassengerEmail().trim().toLowerCase());
        booking.setPassengerPhone(request.getPassengerPhone().trim());
        booking.setJourneySchedule(journey);
        booking.setPickupStop(request.getPickupStop());
        booking.setDropoffStop(request.getDropoffStop());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setTotalDistanceKm(journey.getRoute().getDistanceKm());
        booking.setBaseFare(fareQuote.getSubtotalBaseFare());
        booking.setSeatSurcharges(fareQuote.getTotalSeatSurcharges());
        booking.setTaxes(fareQuote.getTaxAmount());
        booking.setDiscountAmount(fareQuote.getDiscountAmount());
        booking.setTotalFare(fareQuote.getGrandTotal());
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        Map<Long, JourneySeat> seatMap = lockedSeats.stream().collect(Collectors.toMap(JourneySeat::getId, js -> js));

        // Create individual passenger tickets and mark seats as BOOKED
        for (BookingDtos.PassengerDetailDto pDto : request.getPassengers()) {
            JourneySeat js = seatMap.get(pDto.getJourneySeatId());
            js.setStatus(SeatStatus.BOOKED);
            journeySeatRepository.save(js);

            String ticketNumber = "TKT-" + pnr + "-" + js.getSeat().getSeatNumber();
            Double seatPrice = js.getPrice();

            BookingPassenger bp = new BookingPassenger(
                    pDto.getPassengerName().trim(),
                    pDto.getAge(),
                    pDto.getGender(),
                    js.getSeat().getSeatNumber(),
                    seatPrice,
                    ticketNumber,
                    js
            );
            booking.addPassenger(bp);
        }

        Booking savedBooking = bookingRepository.save(booking);

        // Audit Log
        auditLogRepository.save(new AuditLog(
                "CREATE_BOOKING", "Booking", savedBooking.getId(),
                user != null ? user.getEmail() : savedBooking.getPassengerEmail(),
                "Confirmed booking PNR: " + pnr + " for " + lockedSeats.size() + " seats. Total: $" + savedBooking.getTotalFare()
        ));

        return mapToBookingConfirmation(savedBooking);
    }

    @Transactional
    public BookingDtos.BookingConfirmationResponse cancelBooking(Long bookingId, BookingDtos.CancelBookingRequest request, String actor) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("This booking has already been cancelled.");
        }

        FareService.CancellationQuote quote = fareService.calculateCancellationRefund(booking);

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancellationFee(quote.cancellationFee);
        booking.setRefundAmount(quote.refundAmount);
        booking.setCancellationDateTime(LocalDateTime.now());
        booking.setPaymentStatus(quote.refundAmount > 0 ? PaymentStatus.REFUNDED : PaymentStatus.PAID);

        int cancelledSeatsCount = 0;
        // RELEASE SEATS ATOMICALLY
        for (BookingPassenger bp : booking.getPassengers()) {
            bp.setTicketStatus("CANCELLED");
            JourneySeat js = bp.getJourneySeat();
            if (js != null) {
                js.setStatus(SeatStatus.AVAILABLE);
                journeySeatRepository.save(js);
                cancelledSeatsCount++;
            }
        }

        Booking saved = bookingRepository.save(booking);

        // Record Cancellation Audit
        Cancellation cancellation = new Cancellation(
                saved,
                request != null && request.getReason() != null ? request.getReason() : "Customer requested cancellation",
                saved.getTotalFare(),
                quote.cancellationFee,
                quote.refundAmount,
                cancelledSeatsCount,
                actor != null ? actor : "Passenger"
        );
        cancellationRepository.save(cancellation);

        auditLogRepository.save(new AuditLog(
                "CANCEL_BOOKING", "Booking", saved.getId(),
                actor != null ? actor : saved.getPassengerEmail(),
                "Cancelled booking PNR: " + saved.getBookingReference() + ". Fee: $" + quote.cancellationFee + ", Refund: $" + quote.refundAmount
        ));

        return mapToBookingConfirmation(saved);
    }

    public BookingDtos.CancelInquiryResponse getCancellationQuote(String pnr) {
        Booking booking = bookingRepository.findByBookingReference(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + pnr));

        FareService.CancellationQuote quote = fareService.calculateCancellationRefund(booking);

        BookingDtos.CancelInquiryResponse res = new BookingDtos.CancelInquiryResponse();
        res.setBookingReference(booking.getBookingReference());
        res.setDepartureDateTime(booking.getJourneySchedule().getDepartureDateTime());
        res.setHoursUntilDeparture(Math.round(quote.hoursUntilDeparture * 10.0) / 10.0);
        res.setTotalFare(booking.getTotalFare());
        res.setCancellationFeePercentage(quote.feePercentage);
        res.setCancellationFeeAmount(quote.cancellationFee);
        res.setRefundAmount(quote.refundAmount);
        res.setEligible(quote.eligible);
        res.setPolicyDescription(quote.policyDescription);

        return res;
    }

    public BookingDtos.BookingConfirmationResponse getBookingByReference(String pnr) {
        Booking booking = bookingRepository.findByBookingReference(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + pnr));
        return mapToBookingConfirmation(booking);
    }

    public BookingDtos.BookingConfirmationResponse lookupBooking(String pnr, String email) {
        Booking booking = bookingRepository.findByBookingReferenceAndPassengerEmailIgnoreCase(pnr.trim(), email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No matching booking found for PNR " + pnr + " and email " + email));
        return mapToBookingConfirmation(booking);
    }

    public List<BookingDtos.BookingConfirmationResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateTimeDesc(userId).stream()
                .map(this::mapToBookingConfirmation)
                .collect(Collectors.toList());
    }

    public BookingDtos.BookingConfirmationResponse mapToBookingConfirmation(Booking booking) {
        JourneySchedule journey = booking.getJourneySchedule();
        Route route = journey.getRoute();
        Vehicle vehicle = journey.getVehicle();

        List<BookingDtos.TicketDetailDto> tickets = booking.getPassengers().stream()
                .map(bp -> {
                    BookingDtos.TicketDetailDto t = new BookingDtos.TicketDetailDto();
                    t.setTicketNumber(bp.getTicketNumber());
                    t.setPassengerName(bp.getPassengerName());
                    t.setAge(bp.getAge());
                    t.setGender(bp.getGender());
                    t.setSeatNumber(bp.getSeatNumber());
                    if (bp.getJourneySeat() != null && bp.getJourneySeat().getSeat() != null) {
                        t.setDeck(bp.getJourneySeat().getSeat().getDeck().name());
                        t.setSeatType(bp.getJourneySeat().getSeat().getSeatType().name());
                    }
                    t.setIndividualFare(bp.getIndividualFare());
                    t.setTicketStatus(bp.getTicketStatus());
                    // QR payload: machine-readable validation string
                    t.setQrPayload("BUSPASS:" + bp.getTicketNumber() + ":" + booking.getBookingReference() + ":" + bp.getSeatNumber());
                    return t;
                })
                .collect(Collectors.toList());

        BookingDtos.BookingConfirmationResponse res = new BookingDtos.BookingConfirmationResponse();
        res.setBookingId(booking.getId());
        res.setBookingReference(booking.getBookingReference());
        res.setBookingDateTime(booking.getBookingDateTime());
        res.setBookingStatus(booking.getBookingStatus().name());
        res.setPaymentStatus(booking.getPaymentStatus().name());
        res.setPassengerName(booking.getPassengerName());
        res.setPassengerEmail(booking.getPassengerEmail());
        res.setPassengerPhone(booking.getPassengerPhone());
        res.setJourneyId(journey.getId());
        res.setSourceCity(route.getSourceCity());
        res.setDestinationCity(route.getDestinationCity());
        res.setDistanceKm(route.getDistanceKm());
        res.setBusName(vehicle.getBusName());
        res.setBusType(vehicle.getBusType().getDisplayName());
        res.setRegistrationNumber(vehicle.getRegistrationNumber());
        res.setDepartureDateTime(journey.getDepartureDateTime());
        res.setArrivalDateTime(journey.getArrivalDateTime());
        res.setPickupStop(booking.getPickupStop());
        res.setDropoffStop(booking.getDropoffStop());
        res.setBaseFare(booking.getBaseFare());
        res.setSeatSurcharges(booking.getSeatSurcharges());
        res.setTaxes(booking.getTaxes());
        res.setDiscountAmount(booking.getDiscountAmount());
        res.setTotalFare(booking.getTotalFare());
        res.setCancellationFee(booking.getCancellationFee());
        res.setRefundAmount(booking.getRefundAmount());
        res.setTickets(tickets);

        return res;
    }

    private String generateUniquePnr() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("PNR-");
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String pnr = sb.toString();
        if (bookingRepository.findByBookingReference(pnr).isPresent()) {
            return generateUniquePnr();
        }
        return pnr;
    }
}
