package dao;

import conn.DatabaseConnector;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDAO implements GenericDAO<Author> {

    @Override
    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY last_name";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                authors.add(new Author(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                ));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return authors;
    }

    @Override
    public Optional<Author> getById(int id) {
        return Optional.empty();
    }

    @Override
    public boolean save(Author author) {
        String sql = "INSERT INTO authors (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            System.err.println("Chyba při ukládání autora: " + sqlException.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Author author) {
        String sql = "UPDATE authors SET first_name = ?, last_name = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, author.getFirstName());
            preparedStatement.setString(2, author.getLastName());
            preparedStatement.setInt(3, author.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            System.err.println("Chyba při aktualizaci autora: " + sqlException.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            System.err.println("Chyba při mazání autora (asi má vazbu na knihu): " + sqlException.getMessage());
        }
        return false;
    }
}