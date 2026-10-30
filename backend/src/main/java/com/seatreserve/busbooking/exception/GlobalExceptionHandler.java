package com.seatreserve.busbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ErrorDetails {
        private LocalDateTime timestamp = LocalDateTime.now();
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;

        public ErrorDetails(int status, String error, String message, String path) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }

    @ExceptionHandler(DoubleBookingConflictException.class)
    public ResponseEntity<ErrorDetails> handleDoubleBookingConflict(DoubleBookingConflictException ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "SEAT_SELECTION_CONFLICT",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorDetails> handleInvalidBooking(InvalidBookingException ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_BOOKING_REQUEST",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorDetails details = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "One or more fields failed validation checks",
                request.getDescription(false).replace("uri=", "")
        );
        details.setValidationErrors(errors);
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                "Invalid email or password",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                "You do not have administrative permission to perform this action",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails details = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
