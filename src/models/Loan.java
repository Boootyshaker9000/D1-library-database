package models;

import java.time.LocalDate;

/**
 * Represents a loan transaction in the library.
 * Links a book and a reader with loan and return dates.
 */
public class Loan {
    private Integer id;
    private LocalDate loanDate;
    private LocalDate returnDate;

    private Book book;
    private Reader reader;

    /**
     * Default constructor.
     */
    public Loan() {}

    /**
     * Constructs a Loan with specified details.
     *
     * @param id the unique identifier of the loan
     * @param book the book being loaned
     * @param reader the reader borrowing the book
     * @param loanDate the date the loan started
     * @param returnDate the expected return date
     */
    public Loan(Integer id, Book book, Reader reader, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;
        this.book = book;
        this.reader = reader;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }
}