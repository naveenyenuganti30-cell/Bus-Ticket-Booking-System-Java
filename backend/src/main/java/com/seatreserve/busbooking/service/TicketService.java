package com.seatreserve.busbooking.service;

import com.seatreserve.busbooking.exception.ResourceNotFoundException;
import com.seatreserve.busbooking.model.dto.BookingDtos;
import com.seatreserve.busbooking.model.entity.Booking;
import com.seatreserve.busbooking.model.entity.BookingPassenger;
import com.seatreserve.busbooking.model.entity.JourneySchedule;
import com.seatreserve.busbooking.repository.BookingPassengerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TicketService {

    private final BookingPassengerRepository bookingPassengerRepository;

    public TicketService(BookingPassengerRepository bookingPassengerRepository) {
        this.bookingPassengerRepository = bookingPassengerRepository;
    }

    public TicketVerificationResponse getTicketDetails(String ticketNumber) {
        BookingPassenger bp = bookingPassengerRepository.findByTicketNumber(ticketNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketNumber));

        Booking booking = bp.getBooking();
        JourneySchedule journey = booking.getJourneySchedule();

        boolean isExpired = journey.getArrivalDateTime().isBefore(LocalDateTime.now());
        String validityStatus;
        if ("CANCELLED".equalsIgnoreCase(bp.getTicketStatus()) || "CANCELLED".equalsIgnoreCase(booking.getBookingStatus().name())) {
            validityStatus = "CANCELLED";
        } else if (isExpired) {
            validityStatus = "EXPIRED";
        } else {
            validityStatus = "VALID";
        }

        TicketVerificationResponse res = new TicketVerificationResponse();
        res.setTicketNumber(bp.getTicketNumber());
        res.setBookingReference(booking.getBookingReference());
        res.setPassengerName(bp.getPassengerName());
        res.setAge(bp.getAge());
        res.setGender(bp.getGender());
        res.setSeatNumber(bp.getSeatNumber());
        res.setBusName(journey.getVehicle().getBusName());
        res.setBusType(journey.getVehicle().getBusType().getDisplayName());
        res.setRegistrationNumber(journey.getVehicle().getRegistrationNumber());
        res.setSourceCity(journey.getRoute().getSourceCity());
        res.setDestinationCity(journey.getRoute().getDestinationCity());
        res.setDepartureDateTime(journey.getDepartureDateTime());
        res.setArrivalDateTime(journey.getArrivalDateTime());
        res.setPickupStop(booking.getPickupStop());
        res.setDropoffStop(booking.getDropoffStop());
        res.setIndividualFare(bp.getIndividualFare());
        res.setValidityStatus(validityStatus);
        res.setQrPayload("BUSPASS:" + bp.getTicketNumber() + ":" + booking.getBookingReference() + ":" + bp.getSeatNumber());

        return res;
    }

    public static class TicketVerificationResponse {
        private String ticketNumber;
        private String bookingReference;
        private String passengerName;
        private Integer age;
        private String gender;
        private String seatNumber;
        private String busName;
        private String busType;
        private String registrationNumber;
        private String sourceCity;
        private String destinationCity;
        private LocalDateTime departureDateTime;
        private LocalDateTime arrivalDateTime;
        private String pickupStop;
        private String dropoffStop;
        private Double individualFare;
        private String validityStatus;
        private String qrPayload;

        public TicketVerificationResponse() {}

        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getSeatNumber() { return seatNumber; }
        public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
        public String getBusName() { return busName; }
        public void setBusName(String busName) { this.busName = busName; }
        public String getBusType() { return busType; }
        public void setBusType(String busType) { this.busType = busType; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getSourceCity() { return sourceCity; }
        public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }
        public String getDestinationCity() { return destinationCity; }
        public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
        public LocalDateTime getDepartureDateTime() { return departureDateTime; }
        public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }
        public LocalDateTime getArrivalDateTime() { return arrivalDateTime; }
        public void setArrivalDateTime(LocalDateTime arrivalDateTime) { this.arrivalDateTime = arrivalDateTime; }
        public String getPickupStop() { return pickupStop; }
        public void setPickupStop(String pickupStop) { this.pickupStop = pickupStop; }
        public String getDropoffStop() { return dropoffStop; }
        public void setDropoffStop(String dropoffStop) { this.dropoffStop = dropoffStop; }
        public Double getIndividualFare() { return individualFare; }
        public void setIndividualFare(Double individualFare) { this.individualFare = individualFare; }
        public String getValidityStatus() { return validityStatus; }
        public void setValidityStatus(String validityStatus) { this.validityStatus = validityStatus; }
        public String getQrPayload() { return qrPayload; }
        public void setQrPayload(String qrPayload) { this.qrPayload = qrPayload; }
    }
}
