package dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T> {
    Optional<T> getById(int id);
    List<T> getAll();
    boolean save(T t);
    boolean update(T t);
    boolean delete(int id);
}