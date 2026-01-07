package ui.books;

import dao.AuthorDAO;
import dao.BookDAO;
import dao.GenreDAO;
import exceptions.DbException;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialog window for adding or editing a book.
 * Contains form fields for title, price, condition, author, genre, and availability.
 */
public class BookFormDialog extends JDialog {

    private JTextField titleField;
    private JTextField priceField;
    private JCheckBox availableCheckBox;
    private JComboBox<BookCondition> conditionBox;
    private JComboBox<Author> authorBox;
    private JComboBox<Genre> genreBox;

    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final GenreDAO genreDAO = new GenreDAO();

    private final Book bookToEdit;
    private boolean success = false;

    /**
     * Constructs a dialog for adding a new book.
     *
     * @param owner the parent window
     * @param bookDAO the data access object for books
     */
    public BookFormDialog(Window owner, BookDAO bookDAO) {
        this(owner, bookDAO, null);
    }

    /**
     * Constructs a dialog for editing an existing book.
     *
     * @param owner the parent window
     * @param bookDAO the data access object for books
     * @param bookToEdit the book object to edit, or null for a new book
     */
    public BookFormDialog(Window owner, BookDAO bookDAO, Book bookToEdit) {
        super(owner, bookToEdit == null ? "Add New Book" : "Edit Book", ModalityType.APPLICATION_MODAL);
        this.bookDAO = bookDAO;
        this.bookToEdit = bookToEdit;

        initComponents();
        loadComboData();

        if (bookToEdit != null) {
            fillForm(bookToEdit);
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
        add(new JLabel("Title:"), gridBagConstraints);

        titleField = new JTextField(20);
        gridBagConstraints.gridx = 1;
        add(titleField, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        add(new JLabel("Price:"), gridBagConstraints);

        priceField = new JTextField();
        gridBagConstraints.gridx = 1;
        add(priceField, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        add(new JLabel("Condition:"), gridBagConstraints);

        conditionBox = new JComboBox<>(BookCondition.values());
        gridBagConstraints.gridx = 1;
        add(conditionBox, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 3;
        add(new JLabel("Author:"), gridBagConstraints);

        authorBox = new JComboBox<>();
        gridBagConstraints.gridx = 1;
        add(authorBox, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 4;
        add(new JLabel("Genre:"), gridBagConstraints);

        genreBox = new JComboBox<>();
        gridBagConstraints.gridx = 1;
        add(genreBox, gridBagConstraints);

        gridBagConstraints.gridx = 1; gridBagConstraints.gridy = 5;
        availableCheckBox = new JCheckBox("Available");
        availableCheckBox.setSelected(true);
        add(availableCheckBox, gridBagConstraints);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(actionEvent -> onSave());
        cancelButton.addActionListener(actionEvent -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        add(buttonPanel, gridBagConstraints);
    }

    /**
     * Loads authors and genres from the database into the combo boxes.
     */
    private void loadComboData() {
        try {
            List<Author> authors = authorDAO.getAll();
            for (Author author : authors) {
                authorBox.addItem(author);
            }

            List<Genre> genres = genreDAO.getAll();
            for (Genre genre : genres) {
                genreBox.addItem(genre);
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Failed to load combo data: " + dbException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills the form fields with data from the book being edited.
     *
     * @param book the book object to load data from
     */
    private void fillForm(Book book) {
        titleField.setText(book.getTitle());
        priceField.setText(book.getPrice().toString());
        availableCheckBox.setSelected(book.isAvailable());
        conditionBox.setSelectedItem(book.getCondition());

        selectItemById(authorBox, book.getAuthor().getId());
        selectItemById(genreBox, book.getGenre().getId());
    }

    /**
     * Selects an item in a combo box based on its ID.
     *
     * @param comboBox the combo box to select from
     * @param id the ID of the item to select
     * @param <Item> the type of items in the combo box
     */
    private <Item> void selectItemById(JComboBox<Item> comboBox, int id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Item item = comboBox.getItemAt(i);
            if (item instanceof Author author && author.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
            if (item instanceof Genre genre && genre.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Validates the input and saves the book to the database.
     * Handles both creating new books and updating existing ones.
     */
    private void onSave() {
        String title = titleField.getText().trim();
        String priceText = priceField.getText().trim();

        if (title.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Price are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Author selectedAuthor = (Author) authorBox.getSelectedItem();
        Genre selectedGenre = (Genre) genreBox.getSelectedItem();
        BookCondition selectedCondition = (BookCondition) conditionBox.getSelectedItem();

        if (selectedAuthor == null || selectedGenre == null) {
            JOptionPane.showMessageDialog(this, "Please select Author and Genre.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Book bookToSave = new Book();
        bookToSave.setTitle(title);
        bookToSave.setPrice(price);
        bookToSave.setAvailable(availableCheckBox.isSelected());
        bookToSave.setCondition(selectedCondition);
        bookToSave.setAuthor(selectedAuthor);
        bookToSave.setGenre(selectedGenre);

        try {
            if (bookToEdit == null) {
                bookDAO.save(bookToSave);
            } else {
                bookToSave.setId(bookToEdit.getId());
                bookDAO.update(bookToSave);
            }
            success = true;
            dispose();
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, dbException.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns whether the save operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
}