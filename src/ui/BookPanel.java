package ui;

import dao.BookDAO;
import models.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {

    private final BookDAO bookDAO = new BookDAO();
    private JTable bookTable;
    private DefaultTableModel tableModel;

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

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(actionEvent -> refreshTableData());

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(actionEvent -> deleteSelectedBook());

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTableData();
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);

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
    }

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
            if (bookDAO.delete(bookId)) {
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting book. It might be currently on loan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}