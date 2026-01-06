package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import dao.*;
import models.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataImporter {

    private final BookDAO bookDAO = new BookDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void importBooksFromJson(String filePath) {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return;
        }

        try {
            List<Book> books = objectMapper.readValue(file, new TypeReference<>() {
            });

            System.out.println("Found " + books.size() + " books in JSON file. Starting import...");

            int successCount = 0;
            int failCount = 0;

            for (Book book : books) {
                List<String> validationErrors = validateBook(book);

                if (!validationErrors.isEmpty()) {
                    System.err.println("Skipping book '" + (book.getTitle() != null ? book.getTitle() : "UNKNOWN") + "' due to errors:");
                    for (String error : validationErrors) {
                        System.err.println("   - " + error);
                    }
                    failCount++;
                    continue;
                }

                try {
                    processAuthorForBook(book);
                    processGenreForBook(book);
                    bookDAO.save(book);
                    System.out.println("Imported book: " + book.getTitle());
                    successCount++;
                } catch (Exception exception) {
                    System.err.println("Unexpected database error for book " + book.getTitle() + ": " + exception.getMessage());
                    failCount++;
                }
            }

            System.out.println("\n--- IMPORT SUMMARY ---");
            System.out.println("Successfully imported: " + successCount);
            System.out.println("Failed: " + failCount);

        } catch (IOException ioException) {
            System.err.println("Critical error: The JSON file is invalid or unreadable.");
            System.err.println("Details: " + ioException.getMessage());
        }
    }
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

    private void processAuthorForBook(Book book) {
        Author jsonAuthor = book.getAuthor();
        Optional<Author> existingAuthor = authorDAO.findByName(jsonAuthor.getFirstName(), jsonAuthor.getLastName());

        if (existingAuthor.isPresent()) {
            book.setAuthor(existingAuthor.get());
        } else {
            authorDAO.save(jsonAuthor);
        }
    }

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