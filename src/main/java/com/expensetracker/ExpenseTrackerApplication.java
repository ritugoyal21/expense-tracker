package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================
 *  ExpenseTrackerApplication - Entry Point
 *  This is the main class that bootstraps the entire
 *  Spring Boot application.
 * =====================================================
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
        System.out.println("✅ Expense Tracker API is running on http://localhost:8080");
    }
}
