package conf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    private static final String CONFIG_FILE = "conf/config.json";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AppConfiguration loadConfig() {
        try {
            File file = new File(CONFIG_FILE);
            return objectMapper.readValue(file, AppConfiguration.class);
        } catch (IOException ioException) {
            throw new RuntimeException("Error while loading " + CONFIG_FILE + ": " + ioException.getMessage());
        }
    }
}