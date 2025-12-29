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
    @Override public boolean save(Genre genre) { return false; }
    @Override public boolean update(Genre genre) { return false; }
    @Override public boolean delete(int id) { return false; }
}