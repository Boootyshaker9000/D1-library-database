package dao;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenreDAO implements GenericDAO<Genre> {

    @Override
    public List<Genre> getAll() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genres ORDER BY name";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                genres.add(new Genre(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return genres;
    }

    @Override public Optional<Genre> getById(int id) { return Optional.empty(); }
    @Override
    public boolean save(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, genre.getName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        genre.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Chyba při ukládání žánru: " + e.getMessage());
        }
        return false;
    }
    @Override
    public boolean update(Genre genre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, genre.getName());
            preparedStatement.setInt(2, genre.getId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            System.err.println("Chyba při aktualizaci žánru: " + sqlException.getMessage());
        }
        return false;
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM genres WHERE id = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            System.err.println("Chyba při mazání žánru: " + sqlException.getMessage());
        }
        return false;
    }
}