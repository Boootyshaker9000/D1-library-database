package dao;

import conn.DatabaseConnector;
import exceptions.DbException;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for managing Reader entities.
 * Handles database operations such as create, read, update, and delete for readers.
 */
public class ReaderDAO implements GenericDAO<Reader> {

    /**
     * Retrieves all readers from the database.
     *
     * @return a list of all readers
     */
    @Override
    public List<Reader> getAll() {
        List<Reader> readers = new ArrayList<>();
        String query = "select * from readers";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                readers.add(new Reader(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone_number")
                ));
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error loading readers: " + sqlException.getMessage(), sqlException);
        }
        return readers;
    }

    /**
     * Finds a reader by their unique identifier.
     *
     * @param id the ID of the reader
     * @return an Optional containing the reader if found, or empty otherwise
     */
    @Override
    public Optional<Reader> getById(int id) {
        String query = "select * from readers where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Reader(
                            resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("phone_number")
                    ));
                }
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error finding reader ID " + id + ": " + sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    /**
     * Saves a new reader to the database.
     *
     * @param reader the reader entity to save
     * @return true if the operation was successful, false otherwise
     */
    @Override
    public boolean save(Reader reader) {
        String query = "insert into readers (first_name, last_name, phone_number) values (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, reader.getFirstName());
            preparedStatement.setString(2, reader.getLastName());
            preparedStatement.setString(3, reader.getPhoneNumber());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        reader.setId(resultSet.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException sqlException) {
            throw new DbException("Error saving reader: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    /**
     * Updates an existing reader in the database.
     *
     * @param reader the reader entity with updated values
     * @return true if the update was successful, false otherwise
     */
    @Override
    public boolean update(Reader reader) {
        String query = "update readers set first_name = ?, last_name = ?, phone_number = ? where id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, reader.getFirstName());
            preparedStatement.setString(2, reader.getLastName());
            preparedStatement.setString(3, reader.getPhoneNumber());
            preparedStatement.setInt(4, reader.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            throw new DbException("Error updating reader: " + sqlException.getMessage(), sqlException);
        }
    }

    /**
     * Deletes a reader from the database by their ID.
     *
     * @param id the ID of the reader to delete
     * @return true if the reader was deleted, false otherwise
     */
    @Override
    public boolean delete(int id) {
        String query = "delete from readers where id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new DbException("Cannot delete reader. They might have active loans.", sqlException);
        }
    }
}