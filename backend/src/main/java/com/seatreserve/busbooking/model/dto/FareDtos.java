package com.seatreserve.busbooking.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class FareDtos {

    public static class FareCalculationRequest {
        @NotNull
        private Long journeyId;
        @NotEmpty
        private List<Long> seatIds;
        private String promoCode;

        public Long getJourneyId() { return journeyId; }
        public void setJourneyId(Long journeyId) { this.journeyId = journeyId; }
        public List<Long> getSeatIds() { return seatIds; }
        public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
        public String getPromoCode() { return promoCode; }
        public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    }

    public static class SeatPriceBreakdown {
        private String seatNumber;
        private String deck;
        private String seatType;
        private String position;
        private Double baseFare;
        private Double surchargePercentage;
        private Double surchargeAmount;
        private Double totalSeatFare;

        public SeatPriceBreakdown() {}

        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getDeck() { return deck; }
        public void setDeck(String deck) { this.deck = deck; }
        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Double getBaseFare() { return baseFare; }
        public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }
        public Double getSurchargePercentage() { return surchargePercentage; }
        public void setSurchargePercentage(Double surchargePercentage) { this.surchargePercentage = surchargePercentage; }
        public Double getSurchargeAmount() { return surchargeAmount; }
        public void setSurchargeAmount(Double surchargeAmount) { this.surchargeAmount = surchargeAmount; }
        public Double getTotalSeatFare() { return totalSeatFare; }
        public void setTotalSeatFare(Double totalSeatFare) { this.totalSeatFare = totalSeatFare; }
    }

    public static class FareCalculationResponse {
        private Double distanceKm;
        private Double ratePerKm;
        private Double baseFarePerSeat;
        private int totalSeatsCount;
        private Double subtotalBaseFare;
        private Double totalSeatSurcharges;
        private Double subtotalBeforeTax;
        private Double taxPercentage;
        private Double taxAmount;
        private Double discountAmount;
        private Double grandTotal;
        private List<SeatPriceBreakdown> itemizedSeats;

        public FareCalculationResponse() {}

        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        public Double getRatePerKm() { return ratePerKm; }
        public void setRatePerKm(Double ratePerKm) { this.ratePerKm = ratePerKm; }
        public Double getBaseFarePerSeat() { return baseFarePerSeat; }
        public void setBaseFarePerSeat(Double baseFarePerSeat) { this.baseFarePerSeat = baseFarePerSeat; }
        public int getTotalSeatsCount() { return totalSeatsCount; }
        public void setTotalSeatsCount(int totalSeatsCount) { this.totalSeatsCount = totalSeatsCount; }
        public Double getSubtotalBaseFare() { return subtotalBaseFare; }
        public void setSubtotalBaseFare(Double subtotalBaseFare) { this.subtotalBaseFare = subtotalBaseFare; }
        public Double getTotalSeatSurcharges() { return totalSeatSurcharges; }
        public void setTotalSeatSurcharges(Double totalSeatSurcharges) { this.totalSeatSurcharges = totalSeatSurcharges; }
        public Double getSubtotalBeforeTax() { return subtotalBeforeTax; }
        public void setSubtotalBeforeTax(Double subtotalBeforeTax) { this.subtotalBeforeTax = subtotalBeforeTax; }
        public Double getTaxPercentage() { return taxPercentage; }
        public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }
        public Double getTaxAmount() { return taxAmount; }
        public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
        public Double getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
        public Double getGrandTotal() { return grandTotal; }
        public void setGrandTotal(Double grandTotal) { this.grandTotal = grandTotal; }
        public List<SeatPriceBreakdown> getItemizedSeats() { return itemizedSeats; }
        public void setItemizedSeats(List<SeatPriceBreakdown> itemizedSeats) { this.itemizedSeats = itemizedSeats; }
    }
}
