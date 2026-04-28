package com.expensetracker.exception;

/**
 * =====================================================
 *  ResourceNotFoundException
 *
 *  Thrown when a requested expense ID does not exist
 *  in the database. Results in a 404 HTTP response.
 *
 *  RuntimeException = unchecked exception (no try/catch needed)
 * =====================================================
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message Human-readable error message.
     *                Example: "Expense not found with id: 42"
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
