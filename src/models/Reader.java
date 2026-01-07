package models;

/**
 * Represents a library reader (member).
 * Contains personal details such as name and phone number.
 */
public class Reader {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    /**
     * Default constructor.
     */
    public Reader() {}

    /**
     * Constructs a Reader with specified details.
     *
     * @param id the unique identifier of the reader
     * @param firstName the first name
     * @param lastName the last name
     * @param phoneNumber the contact phone number
     */
    public Reader(Integer id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the full name of the reader.
     *
     * @return the first and last name concatenated
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}