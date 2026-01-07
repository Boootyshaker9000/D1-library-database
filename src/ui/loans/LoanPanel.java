package ui.loans;

import dao.LoanDAO;
import exceptions.DbException;
import models.Loan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying and managing loan records.
 * Allows creating new loans, returning books (deleting loans), and refreshing the list.
 */
public class LoanPanel extends JPanel {

    private final LoanDAO loanDAO = new LoanDAO();
    private final JTable loanTable;
    private final DefaultTableModel tableModel;

    /**
     * Constructs the LoanPanel and initializes the table and control buttons.
     */
    public LoanPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Book Title", "Reader Name", "Loan Date", "Return Date"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loanTable = new JTable(tableModel);

        loanTable.removeColumn(loanTable.getColumnModel().getColumn(0));

        add(new JScrollPane(loanTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton newLoanButton = new JButton("New Loan");
        JButton returnBookButton = new JButton("Return Book");
        JButton refreshButton = new JButton("Refresh");

        newLoanButton.addActionListener(actionEvent -> openNewLoanDialog());
        returnBookButton.addActionListener(actionEvent -> returnSelectedBook());
        refreshButton.addActionListener(actionEvent -> refreshData());

        buttonPanel.add(newLoanButton);
        buttonPanel.add(returnBookButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    /**
     * Refreshes the loan data in the table from the database.
     */
    private void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Loan> loans = loanDAO.getAll();
            for (Loan loan : loans) {
                tableModel.addRow(new Object[]{
                        loan.getId(),
                        loan.getBook().getTitle(),
                        loan.getReader().getFirstName() + " " + loan.getReader().getLastName(),
                        loan.getLoanDate(),
                        loan.getReturnDate()
                });
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Error loading loans: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the dialog to create a new loan.
     */
    private void openNewLoanDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        LoanFormDialog dialog = new LoanFormDialog(parent, loanDAO);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            refreshData();
            JOptionPane.showMessageDialog(this, "Loan created successfully.");
        }
    }

    /**
     * Handles the return of a selected book (deletes the loan).
     */
    private void returnSelectedBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to return.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int loanId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Process return for book '" + bookTitle + "'?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (loanDAO.delete(loanId)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this, "Book returned successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Could not return book.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DbException dbException) {
                JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}