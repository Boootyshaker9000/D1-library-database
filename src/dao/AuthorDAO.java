public class AuthorDAO implements GenericDAO<Author> {

    @Override
    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY last_name";

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                authors.add(new Author(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    @Override public Optional<Author> getById(int id) { return Optional.empty(); }
    @Override public boolean save(Author author) { return false; }
    @Override public boolean update(Author author) { return false; }
    @Override public boolean delete(int id) { return false; }