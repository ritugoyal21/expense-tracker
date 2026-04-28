package com.expensetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * =====================================================
 *  Expense Entity
 *  Represents the "expenses" table in MySQL.
 *  Each field maps to a column in the database.
 *
 *  Annotations used:
 *  @Entity       - Marks this as a JPA entity (DB table)
 *  @Table        - Specifies the table name
 *  @Id           - Primary key
 *  @GeneratedValue - Auto-increment ID
 *  @Column       - Configures column constraints
 *  @Enumerated   - Maps enum to DB string/ordinal
 * =====================================================
 */
@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    /**
     * Primary key — auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short title for the expense (e.g., "Lunch at Pizza Hut").
     * Cannot be blank. Max 100 characters.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be under 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Amount spent. Must be > 0.
     * Using BigDecimal for precise monetary values (avoids float rounding errors).
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Expense category (FOOD, TRAVEL, SHOPPING, BILLS, OTHER).
     * Stored as a string in the DB (e.g., "FOOD") for readability.
     */
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    /**
     * Date of the expense. Defaults to today if not provided.
     * Format: YYYY-MM-DD
     */
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Optional description or notes about the expense.
     * Max 255 characters.
     */
    @Size(max = 255, message = "Description must be under 255 characters")
    @Column(length = 255)
    private String description;
}
