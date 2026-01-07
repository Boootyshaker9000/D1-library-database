package dao;

import conn.DatabaseConnector;
import models.LibraryStatistics;
import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for retrieving library statistics.
 * Fetches aggregated data such as total books, readers, and overdue loans.
 */
public class LibraryStatisticsDAO {

    /**
     * Retrieves the library statistics from the database view.
     *
     * @return an Optional containing the LibraryStatistics object if available
     */
    public Optional<LibraryStatistics> getLibraryStatistics() {
        String query = "select * from library_statistics";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

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
            throw new exceptions.DbException("Error loading library statistics: " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }
}