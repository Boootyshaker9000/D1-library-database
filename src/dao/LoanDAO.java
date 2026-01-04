package dao;

import models.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoanDAO {
    public boolean createLoan(Book book, Reader reader) {
        Connection connection = null;

        String insertLoanSql = "INSERT INTO loans (books_id, readers_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
        String updateBookSql = "UPDATE books SET available = 0 WHERE id = ?";

        try {
            connection = DatabaseConnector.getInstance().getConnection();

            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertLoanSql)) {
                LocalDate today = LocalDate.now();
                LocalDate returnDate = today.plusDays(30);

                preparedStatement.setInt(1, book.getId());
                preparedStatement.setInt(2, reader.getId());
                preparedStatement.setDate(3, Date.valueOf(today));
                preparedStatement.setDate(4, Date.valueOf(returnDate));
                preparedStatement.executeUpdate();
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookSql)) {
                preparedStatement.setInt(1, book.getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException sqlException1) {
                    sqlException1.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }
    }
}