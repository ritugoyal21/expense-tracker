package com.expensetracker.dto;

import com.expensetracker.entity.Category;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * =====================================================
 *  ExpenseDTO (Data Transfer Object)
 *
 *  Why use a DTO instead of the Entity directly?
 *  - Separates internal DB model from API contract
 *  - Lets you control exactly what is exposed/accepted
 *  - Prevents accidental over-posting (mass assignment)
 *  - Easy to add/remove fields without changing DB schema
 *
 *  Used for BOTH requests (POST/PUT) and responses (GET).
 * =====================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    /**
     * Included in responses (GET), ignored in requests (POST/PUT).
     * Null when creating a new expense.
     */
    private Long id;

    /**
     * Title of the expense.
     * Example: "Monthly Netflix Subscription"
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be under 100 characters")
    private String title;

    /**
     * How much was spent.
     * Must be a positive value (e.g., 49.99).
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Which category this belongs to.
     * Accepted values: FOOD, TRAVEL, SHOPPING, BILLS, OTHER
     */
    @NotNull(message = "Category is required")
    private Category category;

    /**
     * Date the expense occurred.
     * Format: YYYY-MM-DD (e.g., "2024-12-25")
     */
    @NotNull(message = "Date is required")
    private LocalDate date;

    /**
     * Optional extra notes.
     * Example: "Yearly plan, renews in December"
     */
    @Size(max = 255, message = "Description must be under 255 characters")
    private String description;
}
