package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * =====================================================
 *  ExpenseServiceImpl
 *
 *  Contains all the business logic for expense operations.
 *
 *  Key annotations:
 *  @Service       - Marks this as a Spring service bean
 *  @Transactional - Wraps methods in DB transactions
 *                   (auto-rollback on exception)
 *  @Slf4j         - Provides a 'log' object for logging
 *  @RequiredArgsConstructor - Auto-injects via constructor
 * =====================================================
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    // Injected automatically by Spring via constructor
    private final ExpenseRepository expenseRepository;

    // =====================================================
    //  CREATE
    // =====================================================

    /**
     * Saves a new expense to the database.
     * Converts DTO → Entity → saves → converts back to DTO.
     */
    @Override
    public ExpenseDTO addExpense(ExpenseDTO dto) {
        log.info("Adding new expense: title={}, amount={}", dto.getTitle(), dto.getAmount());

        // Convert the incoming DTO to an Entity for persistence
        Expense expense = mapToEntity(dto);

        // Save to database (INSERT)
        Expense saved = expenseRepository.save(expense);

        log.info("Expense saved with id={}", saved.getId());

        // Convert saved Entity back to DTO and return
        return mapToDTO(saved);
    }

    // =====================================================
    //  READ ALL
    // =====================================================

    /**
     * Fetches all expenses from the database.
     * Converts each Entity to a DTO using Java Streams.
     */
    @Override
    @Transactional(readOnly = true)  // Read-only = no flush needed, better performance
    public List<ExpenseDTO> getAllExpenses() {
        log.info("Fetching all expenses");

        return expenseRepository.findAll()
                .stream()
                .map(this::mapToDTO)   // Convert each Expense → ExpenseDTO
                .collect(Collectors.toList());
    }

    // =====================================================
    //  READ ONE
    // =====================================================

    /**
     * Fetches a single expense by ID.
     * Throws ResourceNotFoundException if not found → 404 response.
     */
    @Override
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        log.info("Fetching expense with id={}", id);

        Expense expense = findExpenseOrThrow(id);
        return mapToDTO(expense);
    }

    // =====================================================
    //  UPDATE
    // =====================================================

    /**
     * Updates an existing expense.
     * Finds it first (throws 404 if missing), then updates all fields.
     */
    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto) {
        log.info("Updating expense with id={}", id);

        // Find existing expense or throw 404
        Expense existing = findExpenseOrThrow(id);

        // Update all fields from the DTO
        existing.setTitle(dto.getTitle());
        existing.setAmount(dto.getAmount());
        existing.setCategory(dto.getCategory());
        existing.setDate(dto.getDate());
        existing.setDescription(dto.getDescription());

        // Save updated entity (Hibernate detects changes automatically)
        Expense updated = expenseRepository.save(existing);

        log.info("Expense updated: id={}", updated.getId());
        return mapToDTO(updated);
    }

    // =====================================================
    //  DELETE
    // =====================================================

    /**
     * Deletes an expense by ID.
     * Throws 404 if expense doesn't exist before attempting deletion.
     */
    @Override
    public void deleteExpense(Long id) {
        log.info("Deleting expense with id={}", id);

        // Ensure it exists (throws 404 if not)
        findExpenseOrThrow(id);

        expenseRepository.deleteById(id);
        log.info("Expense deleted: id={}", id);
    }

    // =====================================================
    //  FILTER
    // =====================================================

    /**
     * Filters expenses based on optional category and/or date range.
     *
     * Logic:
     * - Both category + date range → filter by both
     * - Only category → filter by category only
     * - Only date range → filter by date range only
     * - Nothing provided → return all expenses
     */
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> filterExpenses(Category category, LocalDate startDate, LocalDate endDate) {
        log.info("Filtering expenses: category={}, startDate={}, endDate={}", category, startDate, endDate);

        List<Expense> result;

        boolean hasCategory = category != null;
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasCategory && hasDateRange) {
            // Filter by both category and date range
            result = expenseRepository.findByCategoryAndDateBetween(category, startDate, endDate);

        } else if (hasCategory) {
            // Filter by category only
            result = expenseRepository.findByCategory(category);

        } else if (hasDateRange) {
            // Filter by date range only
            result = expenseRepository.findByDateBetween(startDate, endDate);

        } else {
            // No filters — return everything
            result = expenseRepository.findAll();
        }

        return result.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    //  TOTAL
    // =====================================================

    /**
     * Returns the sum of all expense amounts.
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses() {
        BigDecimal total = expenseRepository.calculateTotalExpenses();
        log.info("Total expenses calculated: {}", total);
        return total;
    }

    // =====================================================
    //  PRIVATE HELPERS
    // =====================================================

    /**
     * Finds an Expense by ID or throws a 404 exception.
     * Reusable helper to avoid duplicating find-or-throw logic.
     */
    private Expense findExpenseOrThrow(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: " + id
                ));
    }

    /**
     * Converts an ExpenseDTO (API model) → Expense entity (DB model).
     * Note: 'id' is not set here — the DB will generate it on save.
     */
    private Expense mapToEntity(ExpenseDTO dto) {
        return Expense.builder()
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .date(dto.getDate())
                .description(dto.getDescription())
                .build();
    }

    /**
     * Converts an Expense entity (DB model) → ExpenseDTO (API model).
     * Includes the generated 'id' so clients can reference it.
     */
    private ExpenseDTO mapToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .date(expense.getDate())
                .description(expense.getDescription())
                .build();
    }
}
