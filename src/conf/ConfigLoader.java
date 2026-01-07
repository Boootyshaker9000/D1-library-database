package conf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for loading application configuration from a JSON file.
 */
public class ConfigLoader {

    private static final String CONFIG_FILE = "res/conf/config.json";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads the configuration settings from the JSON file.
     *
     * @return the AppConfiguration object containing database credentials
     * @throws RuntimeException if the configuration file cannot be read
     */
    public static AppConfiguration loadConfig() {
        try {
            File file = new File(CONFIG_FILE);
            return objectMapper.readValue(file, AppConfiguration.class);
        } catch (IOException ioException) {
            throw new RuntimeException("Error while loading " + CONFIG_FILE + ": " + ioException.getMessage());
        }
    }
}