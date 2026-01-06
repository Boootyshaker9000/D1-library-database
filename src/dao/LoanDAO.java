package dao;

import conn.DatabaseConnector;
import models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDAO {

    public List<Loan> getAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT loans.id AS loan_id, loans.loan_date, loans.return_date,
                   books.id AS book_id, books.title, books.price, books.available, books.condition,
                   authors.id AS author_id, authors.first_name AS author_first, authors.last_name AS author_last,
                   genres.id AS genre_id, genres.name AS genre_name,
                   readers.id AS reader_id, readers.first_name AS reader_first, readers.last_name AS reader_last, readers.phone_number
            FROM loans
            JOIN books ON loans.books_id = books.id
            JOIN authors ON books.author_id = authors.id
            JOIN genres ON books.genre_id = genres.id
            JOIN readers ON loans.readers_id = readers.id
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }

        } catch (SQLException sqlException) {
            System.err.println("Error while loading loans: " + sqlException.getMessage());
        }
        return loans;
    }

    // Metoda pro načtení jedné výpůjčky podle ID
    public Optional<Loan> getById(int id) {
        String sql = """
            SELECT loans.id AS loan_id, loans.loan_date, loans.return_date,
                   books.id AS book_id, books.title, books.price, books.available, books.condition,
                   authors.id AS author_id, authors.first_name AS author_first, authors.last_name AS author_last,
                   genres.id AS genre_id, genres.name AS genre_name,
                   readers.id AS reader_id, readers.first_name AS reader_first, readers.last_name AS reader_last, readers.phone_number
            FROM loans
            JOIN books ON loans.books_id = books.id
            JOIN authors ON books.author_id = authors.id
            JOIN genres ON books.genre_id = genres.id
            JOIN readers ON loans.readers_id = readers.id
            WHERE loans.id = ?
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToLoan(resultSet));
                }
            }
        } catch (SQLException sqlException) {
            System.err.println("Error while finding loan with ID " + id + ": " + sqlException.getMessage());
        }
        return Optional.empty();
    }

    public boolean save(Loan loan) {
        Connection connection = null;
        String insertLoanSql = "INSERT INTO loans (books_id, readers_id, loan_date, return_date) VALUES (?, ?, ?, ?)";
        String updateBookSql = "UPDATE books SET available = 0 WHERE id = ?";

        try {
            connection = DatabaseConnector.getInstance().getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertLoanSql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, loan.getBook().getId());
                preparedStatement.setInt(2, loan.getReader().getId());
                preparedStatement.setDate(3, Date.valueOf(loan.getLoanDate()));
                preparedStatement.setDate(4, Date.valueOf(loan.getReturnDate()));

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            loan.setId(generatedKeys.getInt(1));
                        }
                    }
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookSql)) {
                preparedStatement.setInt(1, loan.getBook().getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException sqlException) {
            System.err.println("Error while saving loan: " + sqlException.getMessage());
            if (connection != null) {
                try {
                    System.err.println("Performing rollback...");
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
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

    // Aktualizace výpůjčky (např. prodloužení data vrácení)
    public boolean update(Loan loan) {
        String sql = "UPDATE loans SET loan_date = ?, return_date = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDate(1, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(2, Date.valueOf(loan.getReturnDate()));
            preparedStatement.setInt(3, loan.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            System.err.println("Error while updating loan: " + sqlException.getMessage());
        }
        return false;
    }

    // Smazání výpůjčky (TRANSAKCE: smaže výpůjčku a uvolní knihu zpět do oběhu)
    public boolean delete(int id) {
        Connection connection = null;
        String selectBookIdSql = "SELECT books_id FROM loans WHERE id = ?";
        String deleteLoanSql = "DELETE FROM loans WHERE id = ?";
        String updateBookSql = "UPDATE books SET available = 1 WHERE id = ?";

        try {
            connection = DatabaseConnector.getInstance().getConnection();
            connection.setAutoCommit(false);

            int bookId = -1;
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectBookIdSql)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        bookId = resultSet.getInt("books_id");
                    }
                }
            }

            if (bookId == -1) {
                return false;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteLoanSql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookSql)) {
                preparedStatement.setInt(1, bookId);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException sqlException) {
            System.err.println("Error while deleting loan: " + sqlException.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackExcepption) {
                    rollbackExcepption.printStackTrace();
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

    private Loan mapResultSetToLoan(ResultSet resultSet) throws SQLException {
        Author author = new Author(
                resultSet.getInt("author_id"),
                resultSet.getString("author_first"),
                resultSet.getString("author_last")
        );

        Genre genre = new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("genre_name")
        );

        Book book = new Book(
                resultSet.getInt("book_id"),
                resultSet.getString("title"),
                resultSet.getBigDecimal("price"),
                resultSet.getBoolean("available"),
                BookCondition.valueOf(resultSet.getString("condition_state")),
                genre,
                author
        );

        Reader reader = new Reader(
                resultSet.getInt("reader_id"),
                resultSet.getString("reader_first"),
                resultSet.getString("reader_last"),
                resultSet.getString("phone_number")
        );

        return new Loan(
                resultSet.getInt("loan_id"),
                book,
                reader,
                resultSet.getDate("loan_date").toLocalDate(),
                resultSet.getDate("return_date").toLocalDate()
        );
    }
}