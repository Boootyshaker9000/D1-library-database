package models;

/**
 * Represents an author of a book.
 */
public class Author {
    private Integer id;
    private String firstName;
    private String lastName;

    /**
     * Default constructor.
     */
    public Author() {}

    /**
     * Constructs an Author with specified details.
     *
     * @param id the unique identifier of the author
     * @param firstName the first name
     * @param lastName the last name
     */
    public Author(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Returns the full name of the author.
     *
     * @return the first and last name concatenated
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}