package models;

import java.math.BigDecimal;

public record LibraryStatistics(
        int totalBooks,
        int availableBooks,
        int totalReaders,
        int overdueLoans,
        BigDecimal totalInventoryValue
) {}