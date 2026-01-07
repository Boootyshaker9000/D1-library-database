package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Author entities.
 * Handles database operations such as retrieving, saving, updating, and deleting authors.
 */
public class AuthorDAO implements GenericDAO<Author> {

    /**
     * Retrieves all authors from the database, ordered by last name.
     *
     * @return a list of all authors
     */
    @Override
    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        String query = "select * from authors order by last_name";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                authors.add(new Author(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ));
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error loading authors: " + sqlException.getMessage(), sqlException);
        }
        return authors;
    }

    /**
     * Finds an author by their unique identifier.
     *
     * @param id the ID of the author
     * @return an Optional containing the author if found, or empty otherwise
     */
    @Override
    public Optional<Author> getById(int id) {
        String query = "select * from authors where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Author(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    ));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error finding author ID " + id + ": " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds an author by their first and last name.
     *
     * @param firstName the first name of the author
     * @param lastName the last name of the author
     * @return an Optional containing the author if found, or empty otherwise
     */
    public Optional<Author> findByName(String firstName, String lastName) {
        String query = "select * from authors where first_name = ? and last_name = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Author(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    ));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error searching for author: " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Saves a new author to the database.
     *
     * @param author the author entity to save
     * @return true if the operation was successful, false otherwise
     */
    @Override
    public boolean save(Author author) {
        String query = "insert into authors (first_name, last_name) values (?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        author.setId(resultSet.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error saving author: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    /**
     * Updates an existing author in the database.
     *
     * @param author the author entity with updated values
     * @return true if the update was successful, false otherwise
     */
    @Override
    public boolean update(Author author) {
        String query = "update authors set first_name = ?, last_name = ? where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setInt(3, author.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            throw new DbException("Error updating author: " + sqlException.getMessage(), sqlException);
        }
    }

    /**
     * Deletes an author from the database by their ID.
     *
     * @param id the ID of the author to delete
     * @return true if the author was deleted, false otherwise
     */
    @Override
    public boolean delete(int id) {
        String query = "delete from authors where id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new DbException("Cannot delete author. They are likely assigned to a book.", sqlException);
        }
    }
}