package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Genre entities.
 * Handles database operations for book genres.
 */
public class GenreDAO implements GenericDAO<Genre> {

    /**
     * Retrieves all genres from the database, ordered by name.
     *
     * @return a list of all genres
     */
    @Override
    public List<Genre> getAll() {
        List<Genre> genres = new ArrayList<>();
        String query = "select * from genres order by name";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                genres.add(new Genre(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error loading genres: " + sqlException.getMessage(), sqlException);
        }
        return genres;
    }

    /**
     * Finds a genre by its unique identifier.
     *
     * @param id the ID of the genre
     * @return an Optional containing the genre if found, or empty otherwise
     */
    @Override
    public Optional<Genre> getById(int id) {
        String query = "select * from genres where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Genre(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error finding genre ID " + id + ": " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Finds a genre by its name.
     *
     * @param name the name of the genre
     * @return an Optional containing the genre if found, or empty otherwise
     */
    public Optional<Genre> findByName(String name) {
        String query = "select * from genres where name = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Genre(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error finding genre by name: " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Saves a new genre to the database.
     *
     * @param genre the genre entity to save
     * @return true if the operation was successful, false otherwise
     */
    @Override
    public boolean save(Genre genre) {
        String query = "insert into genres (name) values (?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, genre.getName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        genre.setId(resultSet.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error saving genre: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    /**
     * Updates an existing genre in the database.
     *
     * @param genre the genre entity with updated values
     * @return true if the update was successful, false otherwise
     */
    @Override
    public boolean update(Genre genre) {
        String query = "update genres set name = ? where id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, genre.getName());
            preparedStatement.setInt(2, genre.getId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new DbException("Error updating genre: " + sqlException.getMessage(), sqlException);
        }
    }

    /**
     * Deletes a genre from the database by its ID.
     *
     * @param id the ID of the genre to delete
     * @return true if the genre was deleted, false otherwise
     */
    @Override
    public boolean delete(int id) {
        String query = "delete from genres where id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new DbException("Cannot delete genre. It is likely used by some books.", sqlException);
        }
    }
}