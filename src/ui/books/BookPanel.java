package ui.books;

import dao.BookDAO;
import exceptions.DbException;
import models.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying and managing books.
 * Shows a table of books and provides buttons for adding, editing, and deleting them.
 */
public class BookPanel extends JPanel {

    private final BookDAO bookDAO = new BookDAO();
    private final JTable bookTable;
    private final DefaultTableModel tableModel;

    /**
     * Constructs the BookPanel and initializes the table and buttons.
     */
    public BookPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Author", "Genre", "Price", "Condition", "Available"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);

        bookTable.removeColumn(bookTable.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createJPanel();

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTableData();
    }

    /**
     * Creates the panel containing action buttons.
     *
     * @return the JPanel with buttons
     */
    private JPanel createJPanel() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(actionEvent -> openAddBookDialog());
        editButton.addActionListener(actionEvent -> openEditBookDialog());
        deleteButton.addActionListener(actionEvent -> deleteSelectedBook());
        refreshButton.addActionListener(actionEvent -> refreshTableData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        return buttonPanel;
    }

    /**
     * Refreshes the table data by fetching the latest list of books from the database.
     */
    private void refreshTableData() {
        tableModel.setRowCount(0);

        try {
            List<Book> books = bookDAO.getAll();

            for (Book book : books) {
                Object[] rowData = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor().getLastName(),
                        book.getGenre().getName(),
                        book.getPrice(),
                        book.getCondition(),
                        book.isAvailable() ? "Yes" : "No"
                };
                tableModel.addRow(rowData);
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the dialog for adding a new book.
     */
    private void openAddBookDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        BookFormDialog dialog = new BookFormDialog(parent, bookDAO);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            refreshTableData();
            JOptionPane.showMessageDialog(this, "Book added successfully.");
        }
    }

    /**
     * Opens the dialog for editing the selected book.
     */
    private void openEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            bookDAO.getById(bookId).ifPresentOrElse(
                    book -> {
                        Window parent = SwingUtilities.getWindowAncestor(this);
                        BookFormDialog dialog = new BookFormDialog(parent, bookDAO, book);
                        dialog.setVisible(true);

                        if (dialog.isSuccess()) {
                            refreshTableData();
                            JOptionPane.showMessageDialog(this, "Book updated successfully.");
                        }
                    },
                    () -> JOptionPane.showMessageDialog(this, "Book not found in DB.", "Error", JOptionPane.ERROR_MESSAGE)
            );
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected book after confirmation.
     */
    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + bookTitle + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookDAO.delete(bookId)) {
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}