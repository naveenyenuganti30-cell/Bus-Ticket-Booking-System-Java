package com.seatreserve.busbooking.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDtos {

    public static class PassengerDetailDto {
        @NotBlank
        private String passengerName;
        @NotNull
        private Integer age;
        @NotBlank
        private String gender;
        @NotNull
        private Long journeySeatId;

        public PassengerDetailDto() {}

        public PassengerDetailDto(String passengerName, Integer age, String gender, Long journeySeatId) {
            this.passengerName = passengerName;
            this.age = age;
            this.gender = gender;
            this.journeySeatId = journeySeatId;
        }

        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public Long getJourneySeatId() { return journeySeatId; }
        public void setJourneySeatId(Long journeySeatId) { this.journeySeatId = journeySeatId; }
    }

    public static class CreateBookingRequest {
        @NotNull
        private Long journeyScheduleId;
        @NotBlank
        private String passengerName;
        @NotBlank @Email
        private String passengerEmail;
        @NotBlank
        private String passengerPhone;
        private String pickupStop;
        private String dropoffStop;
        private String promoCode;

        @NotEmpty
        @Valid
        private List<PassengerDetailDto> passengers;

        public CreateBookingRequest() {}

        public Long getJourneyScheduleId() { return journeyScheduleId; }
        public void setJourneyScheduleId(Long journeyScheduleId) { this.journeyScheduleId = journeyScheduleId; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public String getPassengerEmail() { return passengerEmail; }
        public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
        public String getPassengerPhone() { return passengerPhone; }
        public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
        public String getPickupStop() { return pickupStop; }
        public void setPickupStop(String pickupStop) { this.pickupStop = pickupStop; }
        public String getDropoffStop() { return dropoffStop; }
        public void setDropoffStop(String dropoffStop) { this.dropoffStop = dropoffStop; }
        public String getPromoCode() { return promoCode; }
        public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
        public List<PassengerDetailDto> getPassengers() { return passengers; }
        public void setPassengers(List<PassengerDetailDto> passengers) { this.passengers = passengers; }
    }

    public static class TicketDetailDto {
        private String ticketNumber;
        private String passengerName;
        private Integer age;
        private String gender;
        private String seatNumber;
        private String deck;
        private String seatType;
        private Double individualFare;
        private String ticketStatus;
        private String qrPayload;

        public TicketDetailDto() {}

        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getDeck() { return deck; }
        public void setDeck(String deck) { this.deck = deck; }
        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }
        public Double getIndividualFare() { return individualFare; }
        public void setIndividualFare(Double individualFare) { this.individualFare = individualFare; }
        public String getTicketStatus() { return ticketStatus; }
        public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }
        public String getQrPayload() { return qrPayload; }
        public void setQrPayload(String qrPayload) { this.qrPayload = qrPayload; }
    }

    public static class BookingConfirmationResponse {
        private Long bookingId;
        private String bookingReference;
        private LocalDateTime bookingDateTime;
        private String bookingStatus;
        private String paymentStatus;
        private String passengerName;
        private String passengerEmail;
        private String passengerPhone;
        private Long journeyId;
        private String sourceCity;
        private String destinationCity;
        private Double distanceKm;
        private String busName;
        private String busType;
        private String registrationNumber;
        private LocalDateTime departureDateTime;
        private LocalDateTime arrivalDateTime;
        private String pickupStop;
        private String dropoffStop;
        private Double baseFare;
        private Double seatSurcharges;
        private Double taxes;
        private Double discountAmount;
        private Double totalFare;
        private Double cancellationFee;
        private Double refundAmount;
        private List<TicketDetailDto> tickets;

        public BookingConfirmationResponse() {}

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public LocalDateTime getBookingDateTime() { return bookingDateTime; }
        public void setBookingDateTime(LocalDateTime bookingDateTime) { this.bookingDateTime = bookingDateTime; }
        public String getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public String getPassengerEmail() { return passengerEmail; }
        public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
        public String getPassengerPhone() { return passengerPhone; }
        public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
        public Long getJourneyId() { return journeyId; }
        public void setJourneyId(Long journeyId) { this.journeyId = journeyId; }
        public String getSourceCity() { return sourceCity; }
        public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public String getBusType() { return busType; }
        public void setBusType(String busType) { this.busType = busType; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public LocalDateTime getDepartureDateTime() { return departureDateTime; }
        public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
        public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
        public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }
        public String getPickupStop() { return pickupStop; }
        public void setPickupStop(String pickupStop) { this.pickupStop = pickupStop; }
        public String getDropoffStop() { return dropoffStop; }
        public void setDropoffStop(String dropoffStop) { this.dropoffStop = dropoffStop; }
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
        public Double getCancellationFee() { return cancellationFee; }
        public void setCancellationFee(Double cancellationFee) { this.cancellationFee = cancellationFee; }
        public Double getRefundAmount() { return refundAmount; }
        public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
        public List<TicketDetailDto> getTickets() { return tickets; }
        public void setTickets(List<TicketDetailDto> tickets) { this.tickets = tickets; }
    }

    public static class CancelInquiryResponse {
        private String bookingReference;
        private LocalDateTime departureDateTime;
        private double hoursUntilDeparture;
        private Double totalFare;
        private Double cancellationFeePercentage;
        private Double cancellationFeeAmount;
        private Double refundAmount;
        private boolean isEligible;
        private String policyDescription;

        public CancelInquiryResponse() {}

        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public LocalDateTime getDepartureDateTime() { return departureDateTime; }
        public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
        public double getHoursUntilDeparture() { return hoursUntilDeparture; }
        public void setHoursUntilDeparture(double hoursUntilDeparture) { this.hoursUntilDeparture = hoursUntilDeparture; }
        public Double getTotalFare() { return totalFare; }
        public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }
        public Double getCancellationFeePercentage() { return cancellationFeePercentage; }
        public void setCancellationFeePercentage(Double cancellationFeePercentage) { this.cancellationFeePercentage = cancellationFeePercentage; }
        public Double getCancellationFeeAmount() { return cancellationFeeAmount; }
        public void setCancellationFeeAmount(Double cancellationFeeAmount) { this.cancellationFeeAmount = cancellationFeeAmount; }
        public Double getRefundAmount() { return refundAmount; }
        public void setRefundAmount(Double refundAmount) { this.refundAmount = refundAmount; }
        public boolean isEligible() { return isEligible; }
        public void setEligible(boolean eligible) { isEligible = eligible; }
        public String getPolicyDescription() { return policyDescription; }
        public void setPolicyDescription(String policyDescription) { this.policyDescription = policyDescription; }
    }

    public static class CancelBookingRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
