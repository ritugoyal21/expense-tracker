package com.expensetracker.entity;

/**
 * =====================================================
 *  Category Enum
 *  Defines allowed expense categories.
 *  Using an Enum ensures only valid values are stored.
 * =====================================================
 */
public enum Category {
    FOOD,       // Groceries, restaurants, snacks
    TRAVEL,     // Flights, fuel, hotels, transport
    SHOPPING,   // Clothing, electronics, general retail
    BILLS,      // Rent, electricity, internet, subscriptions
    OTHER       // Anything that doesn't fit above
}
