package ui;

import services.DataImporter;
import ui.authors.AuthorPanel;
import ui.books.BookPanel;
import ui.genres.GenrePanel;
import ui.loans.LoanPanel;
import ui.readers.ReaderPanel;
import ui.statistics.StatisticsPanel;

import javax.swing.*;
import java.io.File;

/**
 * The main application window containing the navigation tabs and the menu bar.
 * This frame serves as the primary container for all application modules.
 */
public class MainFrame extends JFrame {

    /**
     * Constructs the MainFrame, sets up the window properties, menu bar, and tabbed panes.
     */
    public MainFrame() {
        setTitle("Library Database System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", new BookPanel());
        tabbedPane.addTab("Authors", new AuthorPanel());
        tabbedPane.addTab("Genres", new GenrePanel());
        tabbedPane.addTab("Readers", new ReaderPanel());
        tabbedPane.addTab("Loans", new LoanPanel());
        tabbedPane.addTab("Statistics", new StatisticsPanel());

        add(tabbedPane);
    }

    /**
     * Creates the application menu bar with file operations.
     *
     * @return the constructed JMenuBar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem importItem = new JMenuItem("Import JSON...");
        importItem.addActionListener(actionEvent -> performImport());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(actionEvent -> System.exit(0));

        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    /**
     * Handles the logic for importing data from a JSON file.
     * Opens a file chooser, sets up a progress dialog, and runs the import in a separate thread.
     */
    private void performImport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            JDialog logDialog = new JDialog(this, "Import Progress", false);
            logDialog.setSize(600, 400);
            logDialog.setLocationRelativeTo(this);

            JTextArea logArea = new JTextArea();
            logArea.setEditable(false);
            logDialog.add(new JScrollPane(logArea));
            logDialog.setVisible(true);

            new Thread(() -> {
                DataImporter importer = new DataImporter();

                boolean isSuccess = importer.importBooksFromJson(selectedFile.getAbsolutePath(), text -> {
                    SwingUtilities.invokeLater(() -> logArea.append(text));
                });

                SwingUtilities.invokeLater(() -> {
                    logArea.append("\n--- DONE ---\nYou can close this window.");

                    if (isSuccess) {
                        JOptionPane.showMessageDialog(logDialog, "Import finished! Please refresh tables manually.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(logDialog, "Import FAILED! Please check the log for details.", "Import Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start();
        }
    }
}