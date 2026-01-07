package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.ActiveLoans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for retrieving details about active loans.
 * Uses a database view to fetch aggregated data.
 */
public class ActiveLoansDAO {

    /**
     * Retrieves a list of all active loans with detailed information.
     *
     * @return a list of ActiveLoans records
     */
    public List<ActiveLoans> getActiveLoansDetails() {
        List<ActiveLoans> loans = new ArrayList<>();
        String query = "select * from active_loans";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                loans.add(new ActiveLoans(
                        resultSet.getInt("loan_id"),
                        resultSet.getString("book_title"),
                        resultSet.getString("reader_name"),
                        resultSet.getDate("loan_date").toLocalDate(),
                        resultSet.getDate("return_date").toLocalDate(),
                        resultSet.getInt("days_overdue")
                ));
            }

        } catch (SQLException sqlException) {
            throw new DbException("Error loading loan details view: " + sqlException.getMessage(), sqlException);
        }
        return loans;
    }
}