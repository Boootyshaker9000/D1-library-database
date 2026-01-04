package conf;

public record AppConfiguration(
        String dbUrl,
        String dbUser,
        String dbPassword
) {}