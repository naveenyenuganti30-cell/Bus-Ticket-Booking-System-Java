package com.seatreserve.busbooking.controller;

import com.seatreserve.busbooking.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{ticketNumber}")
    public ResponseEntity<TicketService.TicketVerificationResponse> verifyTicket(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(ticketService.getTicketDetails(ticketNumber));
    }
}
