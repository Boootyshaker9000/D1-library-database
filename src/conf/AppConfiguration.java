package conf;

/**
 * A record representing the database configuration.
 *
 * @param dbUrl the JDBC URL of the database
 * @param dbUser the database username
 * @param dbPassword the database password
 */
public record AppConfiguration(
        String dbUrl,
        String dbUser,
        String dbPassword
) {}