package ui.readers;

import dao.ReaderDAO;
import exceptions.DbException;
import models.Reader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing Readers.
 * Displays a list of readers in a table and provides buttons for CRUD operations.
 */
public class ReaderPanel extends JPanel {

    /** Data Access Object for handling Reader persistence. */
    private final ReaderDAO readerDAO = new ReaderDAO();

    /** The table component displaying the list of readers. */
    private final JTable readerTable;

    /** The model backing the reader table. */
    private final DefaultTableModel tableModel;

    /**
     * Constructs the ReaderPanel.
     * Initializes the table model with reader-specific columns,
     * configures the JTable, and sets up the control buttons.
     */
    public ReaderPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "First Name", "Last Name", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        readerTable = new JTable(tableModel);

        // Hide the ID column from view but keep it in the model
        readerTable.removeColumn(readerTable.getColumnModel().getColumn(0));

        add(new JScrollPane(readerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Reader");
        JButton editButton = new JButton("Edit Reader");
        JButton deleteButton = new JButton("Delete Reader");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(actionEvent -> openAddDialog());
        editButton.addActionListener(actionEvent -> openEditDialog());
        deleteButton.addActionListener(actionEvent -> deleteSelectedReader());
        refreshButton.addActionListener(actionEvent -> refreshData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        for(Component button : buttonPanel.getComponents()){
            button.setFocusable(false);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    /**
     * Refreshes the data in the table by fetching the latest list of readers
     * from the database and updating the table model.
     */
    private void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Reader> readers = readerDAO.getAll();
            for (Reader reader : readers) {
                tableModel.addRow(new Object[]{
                        reader.getId(),
                        reader.getFirstName(),
                        reader.getLastName(),
                        reader.getPhoneNumber()
                });
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Error loading readers: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the ReaderFormDialog to add a new reader.
     * Refreshes the table data if the operation was successful.
     */
    private void openAddDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        ReaderFormDialog dialog = new ReaderFormDialog(parent, readerDAO);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            refreshData();
            JOptionPane.showMessageDialog(this, "Reader added successfully.");
        }
    }

    /**
     * Opens the ReaderFormDialog to edit the currently selected reader.
     * Displays a warning if no row is selected.
     */
    private void openEditDialog() {
        int selectedRow = readerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reader to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            readerDAO.getById(id).ifPresentOrElse(
                    reader -> {
                        Window parent = SwingUtilities.getWindowAncestor(this);
                        ReaderFormDialog dialog = new ReaderFormDialog(parent, readerDAO, reader);
                        dialog.setVisible(true);

                        if (dialog.isSuccess()) {
                            refreshData();
                            JOptionPane.showMessageDialog(this, "Reader updated successfully.");
                        }
                    },
                    () -> JOptionPane.showMessageDialog(this, "Reader not found.", "Error", JOptionPane.ERROR_MESSAGE)
            );
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the currently selected reader after user confirmation.
     * Displays a warning if no row is selected.
     */
    private void deleteSelectedReader() {
        int selectedRow = readerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reader to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete reader '" + name + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (readerDAO.delete(id)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Reader deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete reader. Likely has active loans.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}