package models;

/**
 * Represents a book genre (category).
 */
public class Genre {
    private Integer id;
    private String name;

    /**
     * Default constructor.
     */
    public Genre(){}

    /**
     * Constructs a Genre with specified details.
     *
     * @param id the unique identifier of the genre
     * @param name the name of the genre
     */
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /**
     * Returns the name of the genre.
     *
     * @return the genre name
     */
    @Override
    public String toString() {
        return name;
    }
}