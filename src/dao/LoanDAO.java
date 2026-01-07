package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Loan entities.
 * Handles complex database operations involving transactions, joins, and book availability updates.
 */
public class LoanDAO {

    /**
     * Retrieves all loans from the database with detailed information about books, authors, genres, and readers.
     *
     * @return a list of all loans
     */
    public List<Loan> getAll() {
        List<Loan> loans = new ArrayList<>();
        String query = """
            select loans.id as loan_id, loans.loan_date, loans.return_date,
                   books.id as book_id, books.title, books.price, books.available, books.condition,
                   authors.id as author_id, authors.first_name as author_first, authors.last_name as author_last,
                   genres.id as genre_id, genres.name as genre_name,
                   readers.id as reader_id, readers.first_name as reader_first, readers.last_name as reader_last, readers.phone_number
            from loans
            join books on loans.books_id = books.id
            join authors on books.author_id = authors.id
            join genres on books.genre_id = genres.id
            join readers on loans.readers_id = readers.id
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                loans.add(mapResultSetToLoan(resultSet));
            }

        } catch (SQLException sqlException) {
            throw new DbException("Error while loading loans: " + sqlException.getMessage(), sqlException);
        }
        return loans;
    }

    /**
     * Finds a loan by its unique identifier.
     *
     * @param id the ID of the loan
     * @return an Optional containing the loan if found, or empty otherwise
     */
    public Optional<Loan> getById(int id) {
        String query = """
            select loans.id as loan_id, loans.loan_date, loans.return_date,
                   books.id as book_id, books.title, books.price, books.available, books.condition,
                   authors.id as author_id, authors.first_name as author_first, authors.last_name as author_last,
                   genres.id as genre_id, genres.name as genre_name,
                   readers.id as reader_id, readers.first_name as reader_first, readers.last_name as reader_last, readers.phone_number
            from loans
            join books on loans.books_id = books.id
            join authors on books.author_id = authors.id
            join genres on books.genre_id = genres.id
            join readers on loans.readers_id = readers.id
            where loans.id = ?
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToLoan(resultSet));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error while finding loan with ID " + id + ": " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Saves a new loan to the database.
     * This operation runs in a transaction: it inserts the loan record and updates the book's availability to false.
     *
     * @param loan the loan entity to save
     * @return true if the operation was successful
     * @throws DbException if the transaction fails
     */
    public boolean save(Loan loan) {
        Connection connection = null;
        String insertLoanQuery = "insert into loans (books_id, readers_id, loan_date, return_date) values (?, ?, ?, ?)";
        String updateBookQuery = "update books set available = 0 where id = ?";

        try {
            connection = DatabaseConnector.getInstance().getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertLoanQuery, Statement.RETURN_GENERATED_KEYS)) {
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

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookQuery)) {
                preparedStatement.setInt(1, loan.getBook().getId());
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException sqlException) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    throw new DbException("Error during rollback: " + rollbackException.getMessage(), rollbackException);
                }
            }
            throw new DbException("Error while saving loan: " + sqlException.getMessage(), sqlException);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException sqlException) {
                    throw new DbException("Error closing connection: " + sqlException.getMessage(), sqlException);
                }
            }
        }
    }

    /**
     * Updates an existing loan, specifically the dates.
     *
     * @param loan the loan entity with updated values
     * @return true if the update was successful, false otherwise
     */
    public boolean update(Loan loan) {
        String query = "update loans set loan_date = ?, return_date = ? where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDate(1, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(2, Date.valueOf(loan.getReturnDate()));
            preparedStatement.setInt(3, loan.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            throw new DbException("Error while updating loan: " + sqlException.getMessage(), sqlException);
        }
    }

    /**
     * Deletes a loan (returns a book).
     * This operation runs in a transaction: it deletes the loan record and updates the book's availability to true.
     *
     * @param id the ID of the loan to delete
     * @return true if the operation was successful
     * @throws DbException if the transaction fails
     */
    public boolean delete(int id) {
        Connection connection = null;
        String selectBookIdQuery = "select books_id from loans where id = ?";
        String deleteLoanQuery = "delete from loans where id = ?";
        String updateBookQuery = "update books set available = 1 where id = ?";

        try {
            connection = DatabaseConnector.getInstance().getConnection();
            connection.setAutoCommit(false);

            int bookId = -1;
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectBookIdQuery)) {
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

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteLoanQuery)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateBookQuery)) {
                preparedStatement.setInt(1, bookId);
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException sqlException) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackExcepption) {
                    throw new DbException("Error during rollback: " + rollbackExcepption.getMessage(), rollbackExcepption);
                }
            }
            throw new DbException("Error while deleting loan: " + sqlException.getMessage(), sqlException);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException sqlException) {
                    throw new DbException("Error closing connection: " + sqlException.getMessage(), sqlException);
                }
            }
        }
    }

    /**
     * Maps a current row in the ResultSet to a Loan object.
     *
     * @param resultSet the ResultSet cursor
     * @return the mapped Loan object
     * @throws SQLException if a database access error occurs
     */
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
                BookCondition.valueOf(resultSet.getString("condition")),
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