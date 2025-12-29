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
    @Override public boolean save(Reader reader) { return false; }
    @Override public boolean update(Reader reader) { return false; }
    @Override public boolean delete(int id) { return false; }
}