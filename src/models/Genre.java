package models;

public class Genre {
    private Integer id;
    private String name;

    public Genre(){}

    public Genre(Integer id, String genre) {
        this.id = id;
        this.name = genre;
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

    @Override
    public String toString() {
        return name;
    }
}