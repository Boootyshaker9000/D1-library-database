package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import dao.*;
import models.*;
import ui.json.LogOutput;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for parsing JSON files and importing books, authors, and genres into the database.
 * It handles validation and prevents duplicate entries.
 */
public class DataImporter {

    private final BookDAO bookDAO = new BookDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes the DataImporter and configures the Jackson ObjectMapper.
     */
    public DataImporter() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    /**
     * Reads a JSON file and attempts to import the books contained within.
     *
     * @param filePath the absolute path to the JSON file
     * @param logOutput the interface for writing progress logs to the GUI
     * @return true if the file was processed successfully (even with partial import failures), false if the file could not be read
     */
    public boolean importBooksFromJson(String filePath, LogOutput logOutput) {
        File file = new File(filePath);
        if (!file.exists()) {
            logOutput.append("Error: File not found: " + filePath + "\n");
            return false;
        }

        try {
            List<Book> books = objectMapper.readValue(file, new TypeReference<>() {});

            logOutput.append("Found " + books.size() + " books in JSON file. Starting import...\n");

            int successCount = 0;
            int failCount = 0;

            for (Book book : books) {
                List<String> validationErrors = validateBook(book);

                if (!validationErrors.isEmpty()) {
                    String title = (book.getTitle() != null ? book.getTitle() : "UNKNOWN");
                    logOutput.append("Skipping book '" + title + "' due to errors:\n");
                    for (String error : validationErrors) {
                        logOutput.append("   - " + error + "\n");
                    }
                    failCount++;
                    continue;
                }

                try {
                    processAuthorForBook(book);
                    processGenreForBook(book);
                    bookDAO.save(book);
                    logOutput.append("Imported book: " + book.getTitle() + "\n");
                    successCount++;
                } catch (Exception exception) {
                    logOutput.append("Database error for book " + book.getTitle() + ": " + exception.getMessage() + "\n");
                    failCount++;
                }
            }

            logOutput.append("\n--- IMPORT SUMMARY ---\n");
            logOutput.append("Successfully imported: " + successCount + "\n");
            logOutput.append("Failed: " + failCount + "\n");

            return true;

        } catch (IOException ioException) {
            logOutput.append("Critical error: The JSON file is invalid or unreadable.\n");
            logOutput.append("Details: " + ioException.getMessage() + "\n");
            return false;
        }
    }

    /**
     * Validates the mandatory fields of a book object.
     *
     * @param book the book object to validate
     * @return a list of error messages, empty if valid
     */
    private List<String> validateBook(Book book) {
        List<String> errors = new ArrayList<>();

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            errors.add("Book title is missing or empty.");
        }

        if (book.getPrice() == null) {
            errors.add("Price is missing.");
        } else if (book.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Price must be greater than 0.");
        }

        if (book.getCondition() == null) {
            errors.add("Book condition is missing.");
        }

        if (book.getAuthor() == null) {
            errors.add("Author object is missing.");
        } else {
            if (book.getAuthor().getFirstName() == null || book.getAuthor().getFirstName().trim().isEmpty()) {
                errors.add("Author's first name is missing.");
            }
            if (book.getAuthor().getLastName() == null || book.getAuthor().getLastName().trim().isEmpty()) {
                errors.add("Author's last name is missing.");
            }
        }

        if (book.getGenre() == null) {
            errors.add("Genre object is missing.");
        } else {
            if (book.getGenre().getName() == null || book.getGenre().getName().trim().isEmpty()) {
                errors.add("Genre name is missing.");
            }
        }

        return errors;
    }

    /**
     * Checks if the author exists in the database. If so, links the existing author; otherwise, saves the new author.
     *
     * @param book the book containing the author to process
     */
    private void processAuthorForBook(Book book) {
        Author jsonAuthor = book.getAuthor();
        Optional<Author> existingAuthor = authorDAO.findByName(jsonAuthor.getFirstName(), jsonAuthor.getLastName());

        if (existingAuthor.isPresent()) {
            book.setAuthor(existingAuthor.get());
        } else {
            authorDAO.save(jsonAuthor);
        }
    }

    /**
     * Checks if the genre exists in the database. If so, links the existing genre; otherwise, saves the new genre.
     *
     * @param book the book containing the genre to process
     */
    private void processGenreForBook(Book book) {
        Genre jsonGenre = book.getGenre();
        Optional<Genre> existingGenre = genreDAO.findByName(jsonGenre.getName());

        if (existingGenre.isPresent()) {
            book.setGenre(existingGenre.get());
        } else {
            genreDAO.save(jsonGenre);
        }
    }
}