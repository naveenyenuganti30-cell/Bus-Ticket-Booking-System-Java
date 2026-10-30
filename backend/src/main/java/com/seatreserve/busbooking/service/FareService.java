package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.FareDtos;
import com.seatreserve.busbooking.model.entity.*;
import com.seatreserve.busbooking.repository.FareRuleRepository;
import com.seatreserve.busbooking.repository.JourneyScheduleRepository;
import com.seatreserve.busbooking.repository.JourneySeatRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FareService {

    private final JourneyScheduleRepository journeyScheduleRepository;
    private final JourneySeatRepository journeySeatRepository;
    private final FareRuleRepository fareRuleRepository;

    public FareService(JourneyScheduleRepository journeyScheduleRepository,
                       JourneySeatRepository journeySeatRepository,
                       FareRuleRepository fareRuleRepository) {
        this.journeyScheduleRepository = journeyScheduleRepository;
        this.journeySeatRepository = journeySeatRepository;
        this.fareRuleRepository = fareRuleRepository;
    }

    public FareDtos.FareCalculationResponse calculateFare(FareDtos.FareCalculationRequest request) {
        JourneySchedule journey = journeyScheduleRepository.findById(request.getJourneyId())
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + request.getJourneyId()));

        double distanceKm = journey.getRoute().getDistanceKm();
        double ratePerKm = journey.getBaseRatePerKm();
        double baseFarePerSeat = Math.round((distanceKm * ratePerKm) * 100.0) / 100.0;
        double taxPercentage = journey.getServiceTaxPercentage();

        List<JourneySeat> seats = journeySeatRepository.findAllById(request.getSeatIds());
        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats found for provided seat IDs");
        }

        List<FareDtos.SeatPriceBreakdown> itemized = new ArrayList<>();
        double totalBase = 0.0;
        double totalSurcharges = 0.0;

        for (JourneySeat js : seats) {
            Seat seat = js.getSeat();
            double surchargePct = seat.getSurchargePercentage();
            double surchargeAmount = Math.round((baseFarePerSeat * (surchargePct / 100.0)) * 100.0) / 100.0;
            double seatTotal = baseFarePerSeat + surchargeAmount;

            FareDtos.SeatPriceBreakdown item = new FareDtos.SeatPriceBreakdown();
            item.setSeatNumber(seat.getSeatNumber());
            item.setDeck(seat.getDeck().name());
            item.setSeatType(seat.getSeatType().name());
            item.setPosition(seat.getPosition().name());
            item.setBaseFare(baseFarePerSeat);
            item.setSurchargePercentage(surchargePct);
            item.setSurchargeAmount(surchargeAmount);
            item.setTotalSeatFare(seatTotal);

            itemized.add(item);
            totalBase += baseFarePerSeat;
            totalSurcharges += surchargeAmount;
        }

        double subtotal = totalBase + totalSurcharges;
        double taxAmount = Math.round((subtotal * (taxPercentage / 100.0)) * 100.0) / 100.0;

        // Promotional Discount
        double discountAmount = 0.0;
        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            String promo = request.getPromoCode().trim().toUpperCase();
            if ("SAVE10".equals(promo)) {
                discountAmount = Math.round((subtotal * 0.10) * 100.0) / 100.0;
            } else if ("FIRST50".equals(promo)) {
                discountAmount = Math.min(50.0, subtotal);
            } else if ("FESTIVE15".equals(promo)) {
                discountAmount = Math.round((subtotal * 0.15) * 100.0) / 100.0;
            }
        }

        double grandTotal = Math.max(0.0, Math.round((subtotal + taxAmount - discountAmount) * 100.0) / 100.0);

        FareDtos.FareCalculationResponse response = new FareDtos.FareCalculationResponse();
        response.setDistanceKm(distanceKm);
        response.setRatePerKm(ratePerKm);
        response.setBaseFarePerSeat(baseFarePerSeat);
        response.setTotalSeatsCount(seats.size());
        response.setSubtotalBaseFare(totalBase);
        response.setTotalSeatSurcharges(totalSurcharges);
        response.setSubtotalBeforeTax(subtotal);
        response.setTaxPercentage(taxPercentage);
        response.setTaxAmount(taxAmount);
        response.setDiscountAmount(discountAmount);
        response.setGrandTotal(grandTotal);
        response.setItemizedSeats(itemized);

        return response;
    }

    public CancellationQuote calculateCancellationRefund(Booking booking) {
        LocalDateTime departureTime = booking.getJourneySchedule().getDepartureDateTime();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, departureTime);
        double hoursUntilDeparture = duration.toMinutes() / 60.0;

        FareRule rule = fareRuleRepository.findByRuleName("DEFAULT_RULE").orElseGet(() -> {
            FareRule r = new FareRule();
            return fareRuleRepository.save(r);
        });

        double feePercentage;
        String policyDescription;

        if (hoursUntilDeparture > rule.getTier1Hours()) {
            feePercentage = rule.getTier1FeePercent(); // 10%
            policyDescription = "Cancellation > " + rule.getTier1Hours() + "h before departure: " + feePercentage + "% fee, " + (100 - feePercentage) + "% refund.";
        } else if (hoursUntilDeparture > rule.getTier2Hours()) {
            feePercentage = rule.getTier2FeePercent(); // 25%
            policyDescription = "Cancellation between " + rule.getTier2Hours() + "h and " + rule.getTier1Hours() + "h: " + feePercentage + "% fee, " + (100 - feePercentage) + "% refund.";
        } else if (hoursUntilDeparture > rule.getTier3Hours()) {
            feePercentage = rule.getTier3FeePercent(); // 50%
            policyDescription = "Cancellation between " + rule.getTier3Hours() + "h and " + rule.getTier2Hours() + "h: " + feePercentage + "% fee, " + (100 - feePercentage) + "% refund.";
        } else {
            feePercentage = rule.getTier4FeePercent(); // 100%
            policyDescription = "Cancellation < " + rule.getTier3Hours() + "h before departure: Non-refundable (100% cancellation charge).";
        }

        double cancellationFee = Math.round((booking.getTotalFare() * (feePercentage / 100.0)) * 100.0) / 100.0;
        double refundAmount = Math.max(0.0, Math.round((booking.getTotalFare() - cancellationFee) * 100.0) / 100.0);
        boolean eligible = hoursUntilDeparture > 0 && booking.getBookingStatus() == com.seatreserve.busbooking.model.enums.BookingStatus.CONFIRMED;

        return new CancellationQuote(hoursUntilDeparture, feePercentage, cancellationFee, refundAmount, eligible, policyDescription);
    }

    public static class CancellationQuote {
        public final double hoursUntilDeparture;
        public final double feePercentage;
        public final double cancellationFee;
        public final double refundAmount;
        public final boolean eligible;
        public final String policyDescription;

        public CancellationQuote(double hoursUntilDeparture, double feePercentage, double cancellationFee,
                                 double refundAmount, boolean eligible, String policyDescription) {
            this.hoursUntilDeparture = hoursUntilDeparture;
            this.feePercentage = feePercentage;
            this.cancellationFee = cancellationFee;
            this.refundAmount = refundAmount;
            this.eligible = eligible;
            this.policyDescription = policyDescription;
        }
    }
}
