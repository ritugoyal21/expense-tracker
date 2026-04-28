package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * =====================================================
 *  ExpenseRepository
 *
 *  Extends JpaRepository which gives us FREE CRUD methods:
 *  - save(expense)       → INSERT or UPDATE
 *  - findById(id)        → SELECT by ID
 *  - findAll()           → SELECT all
 *  - deleteById(id)      → DELETE by ID
 *  - existsById(id)      → Check if record exists
 *  - count()             → Count all records
 *
 *  We also define CUSTOM queries below for filtering.
 * =====================================================
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all expenses for a specific category.
     * Example: getAllExpensesByCategory(Category.FOOD)
     *
     * Spring auto-generates SQL: SELECT * FROM expenses WHERE category = ?
     */
    List<Expense> findByCategory(Category category);

    /**
     * Find all expenses between two dates (inclusive).
     * Example: find expenses from Jan 1 to Jan 31.
     *
     * Spring auto-generates: SELECT * FROM expenses WHERE date BETWEEN ? AND ?
     */
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by BOTH category AND date range.
     * Custom JPQL query (uses entity field names, not column names).
     *
     * @param category  the category to filter by
     * @param startDate beginning of the date range
     * @param endDate   end of the date range
     */
    @Query("SELECT e FROM Expense e WHERE e.category = :category AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findByCategoryAndDateBetween(
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate the total sum of all expenses.
     * Returns BigDecimal (exact decimal math for money).
     * Returns 0 if no expenses exist (COALESCE prevents null).
     *
     * SQL: SELECT COALESCE(SUM(amount), 0) FROM expenses
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal calculateTotalExpenses();

    /**
     * Calculate total spending for a specific category.
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.category = :category")
    BigDecimal calculateTotalByCategory(@Param("category") Category category);
}
