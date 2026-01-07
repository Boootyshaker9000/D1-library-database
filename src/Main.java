import ui.MainFrame;
import javax.swing.SwingUtilities;

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
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}