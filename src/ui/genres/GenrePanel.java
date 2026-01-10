package ui.genres;

import dao.GenreDAO;
import exceptions.DbException;
import models.Genre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing book genres.
 * Allows adding, editing, and deleting genres.
 */
public class GenrePanel extends JPanel {

    private final GenreDAO genreDAO = new GenreDAO();
    private final JTable genreTable;
    private final DefaultTableModel tableModel;

    /**
     * Constructs the GenrePanel.
     */
    public GenrePanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        genreTable = new JTable(tableModel);
        genreTable.removeColumn(genreTable.getColumnModel().getColumn(0));
        add(new JScrollPane(genreTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Genre");
        JButton editButton = new JButton("Edit Genre");
        JButton deleteButton = new JButton("Delete Genre");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(actionEvent -> openAddDialog());
        editButton.addActionListener(actionEvent -> openEditDialog());
        deleteButton.addActionListener(actionEvent -> deleteSelectedGenre());
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
     * Refreshes the list of genres.
     */
    private void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Genre> genres = genreDAO.getAll();
            for (Genre genre : genres) {
                tableModel.addRow(new Object[]{genre.getId(), genre.getName()});
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage());
        }
    }

    /**
     * Opens the form dialog for adding a genre.
     */
    private void openAddDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        GenreFormDialog dialog = new GenreFormDialog(parent, genreDAO);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            refreshData();
            JOptionPane.showMessageDialog(this, "Genre added successfully.");
        }
    }

    /**
     * Opens the form dialog for adding or editing a genre.
     */
    private void openEditDialog() {
        int selectedRow = genreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a genre to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            genreDAO.getById(id).ifPresentOrElse(
                    genre -> {
                        Window parent = SwingUtilities.getWindowAncestor(this);
                        GenreFormDialog dialog = new GenreFormDialog(parent, genreDAO, genre);
                        dialog.setVisible(true);

                        if (dialog.isSuccess()) {
                            refreshData();
                            JOptionPane.showMessageDialog(this, "Genre updated successfully.");
                        }
                    },
                    () -> JOptionPane.showMessageDialog(this, "Genre not found.", "Error", JOptionPane.ERROR_MESSAGE)
            );
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected genre.
     */
    private void deleteSelectedGenre() {
        int selectedRow = genreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a genre to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete genre '" + name + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (genreDAO.delete(id)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Genre deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete genre. Likely associated with a book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}