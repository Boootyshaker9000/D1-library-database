public interface GenericDAO<T> {
    Optional<T> getById(int id);
    List<T> getAll();
    boolean save(T t);
    boolean update(T t);
    boolean delete(int id);
}