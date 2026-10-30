package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.model.dto.BookingDtos;
import com.seatreserve.busbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDtos.BookingConfirmationResponse> createBooking(
            @Valid @RequestBody BookingDtos.CreateBookingRequest request,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        BookingDtos.BookingConfirmationResponse confirmation = bookingService.createBooking(request, userEmail);
        return new ResponseEntity<>(confirmation, HttpStatus.CREATED);
    }

    @GetMapping("/reference/{pnr}")
    public ResponseEntity<BookingDtos.BookingConfirmationResponse> getBookingByReference(@PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getBookingByReference(pnr));
    }

    @GetMapping("/lookup")
    public ResponseEntity<BookingDtos.BookingConfirmationResponse> lookupBooking(
            @RequestParam String pnr,
            @RequestParam String email) {
        return ResponseEntity.ok(bookingService.lookupBooking(pnr, email));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDtos.BookingConfirmationResponse>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/{pnr}/cancel-quote")
    public ResponseEntity<BookingDtos.CancelInquiryResponse> getCancellationQuote(@PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getCancellationQuote(pnr));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDtos.BookingConfirmationResponse> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) BookingDtos.CancelBookingRequest request,
            Authentication authentication) {
        String actor = authentication != null ? authentication.getName() : "Customer";
        BookingDtos.BookingConfirmationResponse result = bookingService.cancelBooking(id, request, actor);
        return ResponseEntity.ok(result);
    }
}
