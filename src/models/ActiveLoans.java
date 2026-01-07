package models;

import java.time.LocalDate;

/**
 * A record representing a detailed view of an active loan.
 * This is primarily used for the statistics dashboard and reporting.
 *
 * @param loanId the ID of the loan
 * @param bookTitle the title of the borrowed book
 * @param readerName the full name of the reader
 * @param loanDate the date the book was borrowed
 * @param returnDate the date the book is due
 * @param daysOverdue the number of days the loan is overdue (positive if overdue)
 */
public record ActiveLoans(
        int loanId,
        String bookTitle,
        String readerName,
        LocalDate loanDate,
        LocalDate returnDate,
        int daysOverdue
) {
    /**
     * Checks if the loan is currently overdue.
     *
     * @return true if daysOverdue is greater than 0
     */
    public boolean isOverdue() {
        return daysOverdue > 0;
    }
}