package com.expensetracker.service;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * =====================================================
 *  ExpenseService Interface
 *
 *  Defines the "contract" (what methods exist) for the
 *  service layer. The actual implementation is in
 *  ExpenseServiceImpl.
 *
 *  Benefits of using an interface:
 *  - Loose coupling (Controller doesn't know implementation details)
 *  - Easy to swap implementations (e.g., for testing/mocks)
 *  - Follows the Dependency Inversion Principle (SOLID)
 * =====================================================
 */
public interface ExpenseService {

    /**
     * Add a new expense.
     * @param dto the expense details from the request body
     * @return the saved expense with generated ID
     */
    ExpenseDTO addExpense(ExpenseDTO dto);

    /**
     * Retrieve all expenses (no filter).
     * @return list of all expenses
     */
    List<ExpenseDTO> getAllExpenses();

    /**
     * Retrieve a single expense by its ID.
     * @param id the expense ID
     * @return the matching expense
     * @throws ResourceNotFoundException if not found
     */
    ExpenseDTO getExpenseById(Long id);

    /**
     * Update an existing expense.
     * @param id  the ID of the expense to update
     * @param dto the new expense data
     * @return the updated expense
     * @throws ResourceNotFoundException if not found
     */
    ExpenseDTO updateExpense(Long id, ExpenseDTO dto);

    /**
     * Delete an expense by ID.
     * @param id the expense ID to delete
     * @throws ResourceNotFoundException if not found
     */
    void deleteExpense(Long id);

    /**
     * Filter expenses by optional category and/or date range.
     * All parameters are optional — pass null to skip that filter.
     *
     * @param category  filter by this category (or null for all)
     * @param startDate filter from this date (or null)
     * @param endDate   filter up to this date (or null)
     * @return filtered list of expenses
     */
    List<ExpenseDTO> filterExpenses(Category category, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate total spending across all expenses.
     * @return sum of all expense amounts
     */
    BigDecimal getTotalExpenses();
}
