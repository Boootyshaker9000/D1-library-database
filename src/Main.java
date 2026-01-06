import dao.*;
import models.*;

public class Main{
    public static void main(String[] args) {
        System.out.println("--- SPUŠTĚNÍ KOMPLEXNÍHO TESTU ---");

        // 1. TEST AUTORŮ (AuthorDAO)
        System.out.println("\n[1] Test AuthorDAO...");
        AuthorDAO authorDao = new AuthorDAO();

        // Vytvoření nového autora
        Author newAuthor = new Author(null, "Testovací", "Spisovatel");
        if (authorDao.save(newAuthor)) {
            System.out.println("   -> Úspěšně vložen autor: " + newAuthor + " (ID: " + newAuthor.getId() + ")");
        } else {
            System.err.println("   -> Chyba při vkládání autora!");
        }

        // Výpis všech autorů
        System.out.println("   -> Seznam všech autorů v DB:");
        authorDao.getAll().forEach(author -> System.out.println("      - " + author));


        // 2. TEST ŽÁNRŮ (GenreDAO)
        System.out.println("\n[2] Test GenreDAO...");
        GenreDAO genreDao = new GenreDAO();

        // Vytvoření nového žánru
        Genre newGenre = new Genre(null, "Cyberpunk");
        if (genreDao.save(newGenre)) {
            System.out.println("   -> Úspěšně vložen žánr: " + newGenre.getName() + " (ID: " + newGenre.getId() + ")");
        } else {
            System.err.println("   -> Chyba při vkládání žánru!");
        }

        // Výpis žánrů
        genreDao.getAll().forEach(g -> System.out.println("      - " + g.getName()));


        // 3. TEST ČTENÁŘŮ (ReaderDAO)
        System.out.println("\n[3] Test ReaderDAO...");
        ReaderDAO readerDao = new ReaderDAO();

        Reader newReader = new Reader(null, "Petr", "Nový", "+420 999 888 777");
        if (readerDao.save(newReader)) {
            System.out.println("   -> Úspěšně vložen čtenář: " + newReader.toString());
        } else {
            System.err.println("   -> Chyba při vkládání čtenáře!");
        }


        // 4. TEST MAZÁNÍ (Úklid po testu - volitelné)
        System.out.println("\n[4] Test Mazání (Úklid)...");
        if (authorDao.delete(newAuthor.getId())) System.out.println("   -> Autor smazán.");
        if (genreDao.delete(newGenre.getId())) System.out.println("   -> Žánr smazán.");
        if (readerDao.delete(newReader.getId())) System.out.println("   -> Čtenář smazán.");

        System.out.println("\n--- KONEC TESTU ---");
    }
}