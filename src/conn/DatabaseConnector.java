package conn;

import conf.AppConfiguration;
import conf.ConfigLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class responsible for managing the database connection.
 * It loads the configuration and provides a connection instance.
 */
public class DatabaseConnector {

    private static DatabaseConnector instance;
    private Connection connection;

    private final AppConfiguration appConfiguration;

    /**
     * Private constructor to prevent direct instantiation.
     * Loads the application configuration.
     */
    private DatabaseConnector() throws IOException {
        this.appConfiguration = ConfigLoader.loadConfig();
    }

    /**
     * Returns the singleton instance of the DatabaseConnector.
     *
     * @return The singleton instance.
     */
    public static DatabaseConnector getInstance() {
        if (instance == null) {
            try{
                instance = new DatabaseConnector();
            } catch (IOException ignored){}
        }
        return instance;
    }

    /**
     * Retrieves the active database connection.
     * Creates a new connection if one does not exist or is closed.
     *
     * @return The Connection object.
     * @throws SQLException If the connection is closed or invalid.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    appConfiguration.dbUrl(),
                    appConfiguration.dbUser(),
                    appConfiguration.dbPassword()
            );
        }
        return connection;
    }

    /**
     * Checks if the application can connect to the database server.
     * @throws SQLException If the connection to the database server fails.
     */
    public void checkConnection() throws SQLException {
        try (Connection testConn = DriverManager.getConnection(
                appConfiguration.dbUrl(),
                appConfiguration.dbUser(),
                appConfiguration.dbPassword())) {
        }
    }
}