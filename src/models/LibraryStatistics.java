package models;

import java.math.BigDecimal;

/**
 * A record representing aggregated statistics of the library.
 *
 * @param totalBooks total count of books in the library
 * @param availableBooks count of books currently available
 * @param totalReaders total count of registered readers
 * @param overdueLoans count of loans that are past their due date
 * @param totalInventoryValue total monetary value of all books
 */
public record LibraryStatistics(
        int totalBooks,
        int availableBooks,
        int totalReaders,
        int overdueLoans,
        BigDecimal totalInventoryValue
) {}