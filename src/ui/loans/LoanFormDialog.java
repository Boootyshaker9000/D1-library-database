package ui.loans;

import dao.BookDAO;
import dao.LoanDAO;
import dao.ReaderDAO;
import exceptions.DbException;
import models.Book;
import models.Loan;
import models.Reader;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for creating a new loan.
 * Allows selecting a reader and an available book.
 */
public class LoanFormDialog extends JDialog {

    private JComboBox<Reader> readerBox;
    private JComboBox<Book> bookBox;
    private JTextField loanDateField;
    private JTextField returnDateField;

    private final LoanDAO loanDAO;
    private final BookDAO bookDAO = new BookDAO();
    private final ReaderDAO readerDAO = new ReaderDAO();

    private boolean success = false;

    /**
     * Constructs the LoanFormDialog.
     *
     * @param owner the parent window
     * @param loanDAO the DAO for loans
     */
    public LoanFormDialog(Window owner, LoanDAO loanDAO) {
        super(owner, "New Loan", ModalityType.APPLICATION_MODAL);
        this.loanDAO = loanDAO;

        initComponents();
        loadComboData();

        loanDateField.setText(LocalDate.now().toString());
        returnDateField.setText(LocalDate.now().plusDays(30).toString());

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the UI components.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 0;
        add(new JLabel("Reader:"), gridBagConstraints);

        readerBox = new JComboBox<>();
        gridBagConstraints.gridx = 1;
        add(readerBox, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        add(new JLabel("Book:"), gridBagConstraints);

        bookBox = new JComboBox<>();
        gridBagConstraints.gridx = 1;
        add(bookBox, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        add(new JLabel("Loan Date (YYYY-MM-DD):"), gridBagConstraints);

        loanDateField = new JTextField(15);
        gridBagConstraints.gridx = 1;
        add(loanDateField, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 3;
        add(new JLabel("Return Date (YYYY-MM-DD):"), gridBagConstraints);

        returnDateField = new JTextField(15);
        gridBagConstraints.gridx = 1;
        add(returnDateField, gridBagConstraints);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Create Loan");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(actionEvent -> onSave());
        cancelButton.addActionListener(actionEvent -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        add(buttonPanel, gridBagConstraints);
    }

    /**
     * Loads readers and available books into combo boxes.
     */
    private void loadComboData() {
        try {
            List<Reader> readers = readerDAO.getAll();
            for (Reader reader : readers) {
                readerBox.addItem(reader);
            }

            List<Book> books = bookDAO.getAll();
            for (Book book : books) {
                if (book.isAvailable()) {
                    bookBox.addItem(book);
                }
            }

            if (bookBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No books available for loan!", "Warning", JOptionPane.WARNING_MESSAGE);
            }

        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates input and saves the loan.
     */
    private void onSave() {
        Reader selectedReader = (Reader) readerBox.getSelectedItem();
        Book selectedBook = (Book) bookBox.getSelectedItem();

        if (selectedReader == null || selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select Reader and Book.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate loanDate = LocalDate.parse(loanDateField.getText());
            LocalDate returnDate = LocalDate.parse(returnDateField.getText());

            if (returnDate.isBefore(loanDate)) {
                JOptionPane.showMessageDialog(this, "Return date cannot be before loan date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Loan loan = new Loan();
            loan.setBook(selectedBook);
            loan.setReader(selectedReader);
            loan.setLoanDate(loanDate);
            loan.setReturnDate(returnDate);

            loanDAO.save(loan);
            success = true;
            dispose();

        } catch (DateTimeParseException dateTimeParseException) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns true if the loan was successfully created.
     * @return success status
     */
    public boolean isSuccess() {
        return success;
    }
}