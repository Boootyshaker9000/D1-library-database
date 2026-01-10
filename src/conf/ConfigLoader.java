package conf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for loading application configuration from a JSON file.
 */
public class ConfigLoader {

    private static final String CONFIG_FILE = "conf/config.json";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Loads the configuration settings from the JSON file.
     *
     * @return the AppConfiguration object containing database credentials
     * @throws RuntimeException if the configuration file cannot be read
     */
    public static AppConfiguration loadConfig() throws IOException {
        File file = new File(CONFIG_FILE);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return objectMapper.readValue(file, AppConfiguration.class);
    }
}