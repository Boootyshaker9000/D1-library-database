package ui.genres;

import dao.GenreDAO;
import exceptions.DbException;
import models.Genre;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing a Genre.
 */
public class GenreFormDialog extends JDialog {

    private JTextField nameField;
    private final GenreDAO genreDAO;
    private final Genre genreToEdit;
    private boolean success = false;

    /**
     * Constructor for creating a new genre.
     *
     * @param owner the parent window
     * @param genreDAO the DAO for genres
     */
    public GenreFormDialog(Window owner, GenreDAO genreDAO) {
        this(owner, genreDAO, null);
    }

    /**
     * Constructor for editing an existing genre.
     *
     * @param owner the parent window
     * @param genreDAO the DAO for genres
     * @param genreToEdit the genre to edit
     */
    public GenreFormDialog(Window owner, GenreDAO genreDAO, Genre genreToEdit) {
        super(owner, genreToEdit == null ? "Add Genre" : "Edit Genre", ModalityType.APPLICATION_MODAL);
        this.genreDAO = genreDAO;
        this.genreToEdit = genreToEdit;

        initComponents();
        if (genreToEdit != null) nameField.setText(genreToEdit.getName());

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes UI components.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 0;
        add(new JLabel("Genre Name:"), gridBagConstraints);

        nameField = new JTextField(20);
        gridBagConstraints.gridx = 1;
        add(nameField, gridBagConstraints);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(actionEvent -> onSave());
        cancelButton.addActionListener(actionEvent -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        add(buttonPanel, gridBagConstraints);
    }

    /**
     * Validates and saves the genre.
     */
    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Genre genre = new Genre();
        genre.setName(name);

        try {
            if (genreToEdit == null) {
                genreDAO.save(genre);
            } else {
                genre.setId(genreToEdit.getId());
                genreDAO.update(genre);
            }
            success = true;
            dispose();
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns whether the operation was successful.
     * @return true if saved, false otherwise
     */
    public boolean isSuccess() { return success; }
}