package ui.authors;

import dao.AuthorDAO;
import exceptions.DbException;
import models.Author;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing Authors.
 */
public class AuthorPanel extends JPanel {

    private final AuthorDAO authorDAO = new AuthorDAO();
    private final JTable authorTable;
    private final DefaultTableModel tableModel;

    public AuthorPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "First Name", "Last Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        authorTable = new JTable(tableModel);

        authorTable.removeColumn(authorTable.getColumnModel().getColumn(0));

        add(new JScrollPane(authorTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Author");
        JButton editButton = new JButton("Edit Author");
        JButton deleteButton = new JButton("Delete Author");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(actionEvent -> openAddDialog());
        editButton.addActionListener(actionEvent -> openEditDialog());
        deleteButton.addActionListener(actionEvent -> deleteSelectedAuthor());
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

    private void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Author> authors = authorDAO.getAll();
            for (Author author : authors) {
                tableModel.addRow(new Object[]{author.getId(), author.getFirstName(), author.getLastName()});
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Error loading authors: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        AuthorFormDialog dialog = new AuthorFormDialog(parent, authorDAO);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            refreshData();
            JOptionPane.showMessageDialog(this, "Author added successfully.");
        }
    }

    private void openEditDialog() {
        int selectedRow = authorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            authorDAO.getById(id).ifPresentOrElse(
                    author -> {
                        Window parent = SwingUtilities.getWindowAncestor(this);
                        AuthorFormDialog dialog = new AuthorFormDialog(parent, authorDAO, author);
                        dialog.setVisible(true);

                        if (dialog.isSuccess()) {
                            refreshData();
                            JOptionPane.showMessageDialog(this, "Author updated successfully.");
                        }
                    },
                    () -> JOptionPane.showMessageDialog(this, "Author not found.", "Error", JOptionPane.ERROR_MESSAGE)
            );
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedAuthor() {
        int selectedRow = authorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete author '" + name + "'?\nWarning: This might fail if the author has books assigned.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (authorDAO.delete(id)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Author deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete author.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}