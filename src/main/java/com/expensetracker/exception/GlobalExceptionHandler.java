package com.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * =====================================================
 *  GlobalExceptionHandler
 *
 *  Centralized error handling for the entire application.
 *  @RestControllerAdvice intercepts exceptions thrown
 *  from any Controller and converts them into clean
 *  JSON error responses.
 *
 *  Without this, Spring returns ugly HTML error pages.
 *  With this, we return structured JSON like:
 *  {
 *    "status": 404,
 *    "message": "Expense not found with id: 5",
 *    "timestamp": "2024-12-01T10:30:00"
 *  }
 * =====================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --------------------------------------------------
    // 404 - Resource Not Found
    // --------------------------------------------------

    /**
     * Handles ResourceNotFoundException.
     * Returns HTTP 404 with error details.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),    // 404
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // --------------------------------------------------
    // 400 - Validation Errors (e.g., amount < 0)
    // --------------------------------------------------

    /**
     * Handles @Valid annotation failures.
     * Collects all field errors and returns them as a map.
     *
     * Example response:
     * {
     *   "title": "Title is required",
     *   "amount": "Amount must be greater than 0"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // Loop through all field errors and collect them
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // --------------------------------------------------
    // 400 - Illegal Argument (bad input)
    // --------------------------------------------------

    /**
     * Handles IllegalArgumentException (e.g., invalid enum value).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // --------------------------------------------------
    // 500 - Generic / Unexpected Errors
    // --------------------------------------------------

    /**
     * Catch-all handler for any unhandled exception.
     * Prevents leaking internal stack traces to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // =====================================================
    //  Inner class: ErrorResponse
    //  A simple POJO that shapes our JSON error payload.
    // =====================================================

    /**
     * Standard error response structure returned by the API.
     */
    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}
