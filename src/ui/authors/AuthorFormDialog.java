package ui.authors;

import dao.AuthorDAO;
import exceptions.DbException;
import models.Author;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing an Author.
 * This modal dialog allows the user to input first and last names.
 */
public class AuthorFormDialog extends JDialog {

    /** Input field for the author's first name. */
    private JTextField firstNameField;

    /** Input field for the author's last name. */
    private JTextField lastNameField;

    /** Data Access Object for handling Author persistence. */
    private final AuthorDAO authorDAO;

    /** The author object being edited, or null if creating a new author. */
    private final Author authorToEdit;

    /** Flag indicating if the save operation was successful. */
    private boolean success = false;

    /**
     * Constructor for creating a new author.
     * * @param owner     The parent window (owner) of this dialog.
     * @param authorDAO The DAO instance for database operations.
     */
    public AuthorFormDialog(Window owner, AuthorDAO authorDAO) {
        this(owner, authorDAO, null);
    }

    /**
     * Constructor for editing an existing author.
     * * @param owner        The parent window (owner) of this dialog.
     * @param authorDAO    The DAO instance for database operations.
     * @param authorToEdit The Author object to edit, or null to create a new one.
     */
    public AuthorFormDialog(Window owner, AuthorDAO authorDAO, Author authorToEdit) {
        super(owner, authorToEdit == null ? "Add Author" : "Edit Author", ModalityType.APPLICATION_MODAL);
        this.authorDAO = authorDAO;
        this.authorToEdit = authorToEdit;

        initComponents();

        if (authorToEdit != null) {
            fillForm(authorToEdit);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the UI components, sets up the GridBagLayout,
     * and adds action listeners to buttons.
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

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(actionEvent -> onSave());
        cancelButton.addActionListener(actionEvent -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        for(Component button : buttonPanel.getComponents()){
            button.setFocusable(false);
        }

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        add(buttonPanel, gridBagConstraints);
    }

    /**
     * Pre-fills the form fields with data from the existing Author object.
     * * @param author The author whose data should be displayed.
     */
    private void fillForm(Author author) {
        firstNameField.setText(author.getFirstName());
        lastNameField.setText(author.getLastName());
    }

    /**
     * Handles the "Save" button action.
     * Validates input, creates or updates the Author object, and persists it via the DAO.
     * Closes the dialog upon success.
     */
    private void onSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both names are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);

        try {
            if (authorToEdit == null) {
                authorDAO.save(author);
            } else {
                author.setId(authorToEdit.getId());
                authorDAO.update(author);
            }
            success = true;
            dispose();
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks if the dialog action resulted in a successful database update.
     * * @return true if the author was successfully saved or updated, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }
}