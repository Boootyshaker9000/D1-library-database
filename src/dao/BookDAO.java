public class BookDAO implements GenericDAO<Book> {

    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT book.id, book.title, book.price, book.available, book.condition_state,
                   author.id AS author_id, author.first_name, author.last_name,
                   genre.id AS genre_id, genre.name AS genre_name
            FROM books book
            JOIN authors author ON book.author_id = author.id
            JOIN genres genre ON book.genre_id = genre.id
            """;

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
        } catch (SQLException sqlException) {
            System.err.println("Chyba při načítání knih: " + sqlException.getMessage());
        }
        return books;
    }

    @Override
    public boolean save(Book book) {
        String sql = """
            INSERT INTO books (title, price, available, condition_state, genre_id, author_id) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            sqlException.printStackTrace();
        }
        return false;
    }

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
                BookCondition.valueOf(resultSet.getString("condition_state")),
                genre,
                author
        );
    }

    @Override public Optional<Book> getById(int id) { return Optional.empty(); }
    @Override public boolean update(Book book) { return false; }
    @Override public boolean delete(int id) { return false; }
}