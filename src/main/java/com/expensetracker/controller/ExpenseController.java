package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.entity.Category;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================
 *  ExpenseController
 *
 *  Handles all HTTP requests for the /expenses endpoint.
 *  Each method maps to an API endpoint.
 *
 *  Key annotations:
 *  @RestController    - Combines @Controller + @ResponseBody
 *                       (auto-converts return values to JSON)
 *  @RequestMapping    - Base URL path for all endpoints
 *  @Valid             - Triggers bean validation on DTOs
 *  @PathVariable      - Binds URL segment to method param
 *  @RequestParam      - Binds query string params
 *  @RequestBody       - Parses JSON body into Java object
 * =====================================================
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    // Service layer injected via constructor
    private final ExpenseService expenseService;

    // =====================================================
    //  POST /expenses — Create a new expense
    // =====================================================

    /**
     * Add a new expense.
     *
     * Request:  POST /expenses
     * Body:     JSON with title, amount, category, date, description
     * Response: 201 Created + saved expense with generated ID
     *
     * Example Request Body:
     * {
     *   "title": "Groceries",
     *   "amount": 850.50,
     *   "category": "FOOD",
     *   "date": "2024-12-01",
     *   "description": "Weekly grocery shopping"
     * }
     */
    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@Valid @RequestBody ExpenseDTO dto) {
        log.info("POST /expenses - Adding expense: {}", dto.getTitle());
        ExpenseDTO created = expenseService.addExpense(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);  // 201
    }

    // =====================================================
    //  GET /expenses — Get all expenses
    // =====================================================

    /**
     * Retrieve all expenses (unfiltered).
     *
     * Request:  GET /expenses
     * Response: 200 OK + array of all expenses
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        log.info("GET /expenses - Fetching all expenses");
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);  // 200
    }

    // =====================================================
    //  GET /expenses/{id} — Get single expense
    // =====================================================

    /**
     * Retrieve a specific expense by its ID.
     *
     * Request:  GET /expenses/5
     * Response: 200 OK + expense | 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        log.info("GET /expenses/{} - Fetching expense", id);
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);  // 200
    }

    // =====================================================
    //  PUT /expenses/{id} — Update an expense
    // =====================================================

    /**
     * Update an existing expense by its ID.
     *
     * Request:  PUT /expenses/5
     * Body:     Updated JSON fields
     * Response: 200 OK + updated expense | 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDTO dto) {

        log.info("PUT /expenses/{} - Updating expense", id);
        ExpenseDTO updated = expenseService.updateExpense(id, dto);
        return ResponseEntity.ok(updated);  // 200
    }

    // =====================================================
    //  DELETE /expenses/{id} — Delete an expense
    // =====================================================

    /**
     * Delete an expense by its ID.
     *
     * Request:  DELETE /expenses/5
     * Response: 200 OK + success message | 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable Long id) {
        log.info("DELETE /expenses/{} - Deleting expense", id);
        expenseService.deleteExpense(id);

        // Return a meaningful success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expense with id " + id + " deleted successfully");
        return ResponseEntity.ok(response);  // 200
    }

    // =====================================================
    //  GET /expenses/filter — Filter expenses
    // =====================================================

    /**
     * Filter expenses by category and/or date range.
     * All query parameters are optional.
     *
     * Request:  GET /expenses/filter?category=FOOD&startDate=2024-01-01&endDate=2024-12-31
     *
     * Query Params (all optional):
     * - category  : FOOD | TRAVEL | SHOPPING | BILLS | OTHER
     * - startDate : YYYY-MM-DD
     * - endDate   : YYYY-MM-DD
     *
     * Examples:
     * - Filter by category only:    GET /expenses/filter?category=FOOD
     * - Filter by date range only:  GET /expenses/filter?startDate=2024-01-01&endDate=2024-06-30
     * - Filter by both:             GET /expenses/filter?category=TRAVEL&startDate=2024-01-01&endDate=2024-12-31
     * - No filter (all):            GET /expenses/filter
     */
    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseDTO>> filterExpenses(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /expenses/filter - category={}, startDate={}, endDate={}", category, startDate, endDate);
        List<ExpenseDTO> filtered = expenseService.filterExpenses(category, startDate, endDate);
        return ResponseEntity.ok(filtered);  // 200
    }

    // =====================================================
    //  GET /expenses/total — Total spending
    // =====================================================

    /**
     * Get the total sum of all expenses.
     *
     * Request:  GET /expenses/total
     * Response: 200 OK + JSON with total amount
     *
     * Example Response:
     * {
     *   "totalExpenses": 4520.75,
     *   "currency": "INR"
     * }
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalExpenses() {
        log.info("GET /expenses/total - Calculating total");
        BigDecimal total = expenseService.getTotalExpenses();

        // Build response map
        Map<String, Object> response = new HashMap<>();
        response.put("totalExpenses", total);
        response.put("currency", "INR");  // Change to your preferred currency

        return ResponseEntity.ok(response);  // 200
    }
}
