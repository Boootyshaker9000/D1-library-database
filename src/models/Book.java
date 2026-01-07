package models;

import java.math.BigDecimal;

/**
 * Represents a book entity in the library system.
 * Contains details such as title, price, condition, availability, author, and genre.
 */
public class Book {
    private Integer id;
    private String title;
    private BigDecimal price;
    private boolean available;
    private BookCondition condition;

    private Genre genre;
    private Author author;

    /**
     * Default constructor.
     */
    public Book() {}

    /**
     * Constructs a Book with specified details.
     *
     * @param id the unique identifier of the book
     * @param title the title of the book
     * @param price the price of the book
     * @param available the availability status
     * @param condition the physical condition of the book
     * @param genre the genre of the book
     * @param author the author of the book
     */
    public Book(Integer id, String title, BigDecimal price, boolean available, BookCondition condition, Genre genre, Author author) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.available = available;
        this.condition = condition;
        this.genre = genre;
        this.author = author;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public BookCondition getCondition() { return condition; }
    public void setCondition(BookCondition condition) { this.condition = condition; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    /**
     * Returns the title of the book as its string representation.
     *
     * @return the title of the book
     */
    @Override
    public String toString() {
        return title;
    }
}