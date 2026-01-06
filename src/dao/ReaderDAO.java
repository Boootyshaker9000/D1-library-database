package dao;

import conn.DatabaseConnector;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDAO implements GenericDAO<Reader> {

    @Override
    public List<Reader> getAll() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM readers";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                readers.add(new Reader(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone_number")
                ));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return readers;
    }

    @Override public Optional<Reader> getById(int id) { return Optional.empty(); }
    @Override
    public boolean save(Reader reader) {
        String sql = "INSERT INTO readers (first_name, last_name, phone_number) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            System.err.println("Chyba při ukládání čtenáře: " + sqlException.getMessage());
        }
        return false;
    }
    @Override
    public boolean update(Reader reader) {
        String sql = "UPDATE readers SET first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, reader.getFirstName());
            preparedStatement.setString(2, reader.getLastName());
            preparedStatement.setString(3, reader.getPhoneNumber());
            preparedStatement.setInt(4, reader.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Chyba při aktualizaci čtenáře: " + e.getMessage());
        }
        return false;
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM readers WHERE id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            System.err.println("Chyba při mazání čtenáře: " + sqlException.getMessage());
        }
        return false;
    }
}