package dao;

import conn.DatabaseConnector;
import models.LibraryStatistics;
import java.sql.*;
import java.util.Optional;

public class LibraryStatisticsDAO {

    public Optional<LibraryStatistics> getLibraryStatistics() {
        String sql = "SELECT * FROM library_statistics";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return Optional.of(new LibraryStatistics(
                        resultSet.getInt("total_books"),
                        resultSet.getInt("available_books"),
                        resultSet.getInt("total_readers"),
                        resultSet.getInt("overdue_loans"),
                        resultSet.getBigDecimal("total_inventory_value")
                ));
            }

        } catch (SQLException sqlException) {
            System.err.println("Error while loading library statistics: " + sqlException.getMessage());
        }
        return Optional.empty();
    }
}