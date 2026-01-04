package dao;

import conf.AppConfiguration;
import conf.ConfigLoader;
import models.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static DatabaseConnector instance;
    private Connection connection;

    private final AppConfiguration config;

    private DatabaseConnector() {
        this.config = ConfigLoader.loadConfig();
    }

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                config.dbUrl(),
                config.dbUser(),
                config.dbPassword()
            );
        }
        return connection;
    }
}