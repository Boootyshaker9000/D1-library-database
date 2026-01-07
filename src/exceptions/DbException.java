package exceptions;

/**
 * Custom runtime exception for handling database-related errors.
 * This allows checked exceptions (SQLException) to be wrapped and caught at the UI layer
 * without cluttering DAO signatures.
 */
public class DbException extends RuntimeException {

    /**
     * Constructs a new DbException with the specified detail message.
     *
     * @param message The detail message.
     */
    public DbException(String message) {
        super(message);
    }

    /**
     * Constructs a new DbException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause (a Throwable object).
     */
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}