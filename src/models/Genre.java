public enum Genre {
    SCIFI("Sci-Fi"),
    DRAMA("Drama"),
    HORROR("Horor"),
    ROMANCE("Romantika"),
    EDUCATIONAL("Odborná");

    private final String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return genre;
    }
}