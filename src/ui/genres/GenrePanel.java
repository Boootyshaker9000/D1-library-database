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
    private final JTable table;
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

        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(actionEvent -> openDialog(null));
        editButton.addActionListener(actionEvent -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                openDialog(new Genre((int)tableModel.getValueAt(selectedRow, 0), (String)tableModel.getValueAt(selectedRow, 1)));
            }
        });
        deleteButton.addActionListener(actionEvent -> deleteGenre());
        refreshButton.addActionListener(actionEvent -> refresh());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refresh();
    }

    /**
     * Refreshes the list of genres.
     */
    private void refresh() {
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
     * Opens the form dialog for adding or editing a genre.
     *
     * @param genre the genre to edit, or null for a new genre
     */
    private void openDialog(Genre genre) {
        GenreFormDialog genreFormDialog = new GenreFormDialog(SwingUtilities.getWindowAncestor(this), genreDAO, genre);
        genreFormDialog.setVisible(true);
        if (genreFormDialog.isSuccess()) refresh();
    }

    /**
     * Deletes the selected genre.
     */
    private void deleteGenre() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int id = (int) tableModel.getValueAt(selectedRow, 0);

        if (JOptionPane.showConfirmDialog(this, "Delete genre?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (genreDAO.delete(id)) refresh();
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}