package ui.authors;

import dao.AuthorDAO;
import exceptions.DbException;
import models.Author;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing an Author.
 */
public class AuthorFormDialog extends JDialog {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private final AuthorDAO authorDAO;
    private final Author authorToEdit;
    private boolean success = false;

    /**
     * Constructor for new author.
     * @param owner parent window
     * @param authorDAO DAO
     */
    public AuthorFormDialog(Window owner, AuthorDAO authorDAO) {
        this(owner, authorDAO, null);
    }

    /**
     * Constructor for editing author.
     * @param owner parent window
     * @param authorDAO DAO
     * @param authorToEdit author to edit
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

    private void fillForm(Author author) {
        firstNameField.setText(author.getFirstName());
        lastNameField.setText(author.getLastName());
    }

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

    public boolean isSuccess() {
        return success;
    }
}