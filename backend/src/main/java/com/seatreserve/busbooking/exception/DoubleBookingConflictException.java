package com.seatreserve.busbooking.exception;

public class DoubleBookingConflictException extends RuntimeException {
    public DoubleBookingConflictException(String message) {
        super(message);
    }
}
