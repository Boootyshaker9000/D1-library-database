import conf.ConfigLoader;
import conn.DatabaseConnector;
import ui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The entry point of the Library Database System application.
 * It initializes the main application window on the Event Dispatch Thread.
 */
public class Main {

    /**
     * The main method that launches the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ConfigLoader.loadConfig();
            DatabaseConnector.getInstance().checkConnection();

        } catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null,
                    "Critical error: The file 'conf/config.json' probably contains incorrect information!",
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null,
                    "Critical error: The file 'conf/config.json' is empty, keys have been changed or has invalid json format!",
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Throwable throwable) {
                JOptionPane.showMessageDialog(null,
                        "Unexpected error:\n" + throwable.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}