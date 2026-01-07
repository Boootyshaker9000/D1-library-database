package ui.readers;

import dao.ReaderDAO;
import exceptions.DbException;
import models.Reader;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog window for adding or editing a Reader.
 * Includes validation for mandatory fields and phone number format.
 */
public class ReaderFormDialog extends JDialog {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;

    private final ReaderDAO readerDAO;
    private final Reader readerToEdit;
    private boolean success = false;

    /**
     * Constructs a dialog for adding a new reader.
     *
     * @param owner the parent window
     * @param readerDAO the data access object for readers
     */
    public ReaderFormDialog(Window owner, ReaderDAO readerDAO) {
        this(owner, readerDAO, null);
    }

    /**
     * Constructs a dialog for editing an existing reader.
     *
     * @param owner the parent window
     * @param readerDAO the data access object for readers
     * @param readerToEdit the reader entity to edit, or null for creating a new one
     */
    public ReaderFormDialog(Window owner, ReaderDAO readerDAO, Reader readerToEdit) {
        super(owner, readerToEdit == null ? "Add Reader" : "Edit Reader", ModalityType.APPLICATION_MODAL);
        this.readerDAO = readerDAO;
        this.readerToEdit = readerToEdit;

        initComponents();

        if (readerToEdit != null) {
            fillForm(readerToEdit);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the UI components and layout.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 0;
        add(new JLabel("First Name:"), gridBagConstraints);
        firstNameField = new JTextField(20);
        gridBagConstraints.gridx = 1;
        add(firstNameField, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        add(new JLabel("Last Name:"), gridBagConstraints);
        lastNameField = new JTextField(20);
        gridBagConstraints.gridx = 1;
        add(lastNameField, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        add(new JLabel("Phone:"), gridBagConstraints);
        phoneField = new JTextField(20);
        gridBagConstraints.gridx = 1;
        add(phoneField, gridBagConstraints);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(actionEvent -> onSave());
        cancelButton.addActionListener(actionEvent -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        add(buttonPanel, gridBagConstraints);
    }

    /**
     * Fills the form fields with data from the reader object.
     *
     * @param reader the reader object containing data to display
     */
    private void fillForm(Reader reader) {
        firstNameField.setText(reader.getFirstName());
        lastNameField.setText(reader.getLastName());
        phoneField.setText(reader.getPhoneNumber());
    }

    /**
     * Validates input fields and saves the reader to the database.
     * Checks if names are present and if the phone number format is valid.
     */
    private void onSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phoneNumber.isEmpty() && !phoneNumber.matches("^[+]?[0-9\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Invalid phone number format.\nOnly digits, spaces, and an optional leading '+' are allowed.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reader reader = new Reader();
        reader.setFirstName(firstName);
        reader.setLastName(lastName);
        reader.setPhoneNumber(phoneNumber);

        try {
            if (readerToEdit == null) {
                readerDAO.save(reader);
            } else {
                reader.setId(readerToEdit.getId());
                readerDAO.update(reader);
            }
            success = true;
            dispose();
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the success status of the dialog operation.
     *
     * @return true if the reader was saved successfully, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
}