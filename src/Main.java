import conf.ConfigLoader;
import conn.DatabaseConnector;
import ui.MainFrame;

import javax.swing.*;
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
            String errorDetail = sqlException.getMessage();
            JOptionPane.showMessageDialog(null,
                    "Critical error: The file 'config.json' probably contains incorrect information!\n\n" +
                            "The app cannot connect to database.\n" +
                            "Error detail: " + errorDetail,
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