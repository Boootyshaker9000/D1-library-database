package dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for Data Access Objects.
 * Defines standard CRUD operations.
 *
 * @param <T> the type of the model entity
 */
public interface GenericDAO<T> {
    /**
     * Retrieves an entity by its ID.
     * @param id the unique identifier
     * @return an Optional containing the entity if found
     */
    Optional<T> getById(int id);

    /**
     * Retrieves all entities of type T.
     * @return a list of entities
     */
    List<T> getAll();

    /**
     * Saves a new entity to the data source.
     * @param entity the entity to save
     * @return true if successful
     */
    boolean save(T entity);

    /**
     * Updates an existing entity.
     * @param entity the entity with updated values
     * @return true if successful
     */
    boolean update(T entity);

    /**
     * Deletes an entity by its ID.
     * @param id the unique identifier
     * @return true if successful
     */
    boolean delete(int id);
}