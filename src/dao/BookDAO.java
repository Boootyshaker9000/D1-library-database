package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Book entities.
 * Handles database operations such as create, read, update, and delete for books.
 */
public class BookDAO implements GenericDAO<Book> {

    /**
     * Retrieves all books from the database with their associated authors and genres.
     *
     * @return a list of all books
     */
    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String query = """
            select book.id, book.title, book.price, book.available, book.condition,
                   author.id as author_id, author.first_name, author.last_name,
                   genre.id as genre_id, genre.name as genre_name
            from books book
            join authors author on book.author_id = author.id
            join genres genre on book.genre_id = genre.id
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error loading books: " + sqlException.getMessage(), sqlException);
        }
        return books;
    }

    /**
     * Saves a new book to the database.
     *
     * @param book the book entity to save
     * @return true if the operation was successful, false otherwise
     */
    @Override
    public boolean save(Book book) {
        String query = """
            insert into books (title, price, available, `condition`, genre_id, author_id) 
            values (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            preparedStatement.setBoolean(3, book.isAvailable());
            preparedStatement.setString(4, book.getCondition().name());
            preparedStatement.setInt(5, book.getGenre().getId());
            preparedStatement.setInt(6, book.getAuthor().getId());

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error saving book: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    /**
     * Maps a current row in the ResultSet to a Book object.
     *
     * @param resultSet the ResultSet cursor
     * @return the mapped Book object
     * @throws SQLException if a database access error occurs
     */
    private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
        Author author = new Author(
                resultSet.getInt("author_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );

        Genre genre = new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("genre_name")
        );

        return new Book(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getBigDecimal("price"),
                resultSet.getBoolean("available"),
                BookCondition.valueOf(resultSet.getString("condition")),
                genre,
                author
        );
    }

    /**
     * Finds a book by its unique identifier.
     *
     * @param id the ID of the book
     * @return an Optional containing the book if found, or empty otherwise
     */
    @Override
    public Optional<Book> getById(int id) {
        String query = """
        select book.id, book.title, book.price, book.available, book.condition,
               author.id as author_id, author.first_name, author.last_name,
               genre.id as genre_id, genre.name as genre_name
        from books book
        join authors author on book.author_id = author.id
        join genres genre on book.genre_id = genre.id
        where book.id = ?
        """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToBook(resultSet));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error finding book ID " + id + ": " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Updates an existing book in the database.
     *
     * @param book the book entity with updated values
     * @return true if the update was successful, false otherwise
     */
    @Override
    public boolean update(Book book) {
        String query = "update books set title = ?, price = ?, available = ?, `condition` = ?, genre_id = ?, author_id = ? where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            preparedStatement.setBoolean(3, book.isAvailable());
            preparedStatement.setString(4, book.getCondition().name());
            preparedStatement.setInt(5, book.getGenre().getId());
            preparedStatement.setInt(6, book.getAuthor().getId());
            preparedStatement.setInt(7, book.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            throw new DbException("Error updating book: " + sqlException.getMessage(), sqlException);
        }
    }

    /**
     * Deletes a book from the database by its ID.
     *
     * @param id the ID of the book to delete
     * @return true if the book was deleted, false otherwise
     */
    @Override
    public boolean delete(int id) {
        String query = "delete from books where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            throw new DbException("Cannot delete book. It is likely linked to existing loans.", sqlException);
        }
    }
}