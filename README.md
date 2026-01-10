# Library database manager
## Program's purpose
This application serves for managing a library database. It connects to a MySQL database and let's you view, add, update and delete data such as info about books, readers, loans, etc.
Requirements
[Java 17](https://www.oracle.com/java/technologies/downloads/#jdk21-windows)
[MySQL 8.0.44](https://dev.mysql.com/downloads/installer/)
The user whos credentials are filled in the *conf/config.json* file must have priviliges for reading, writing, updating and deleting to use the app as intended.
## How to run program
### Configuration
The app loads credentials from *conf/config.json*. The content of the file may look something like this:
```json
{
  "dbUrl": "jdbc:mysql://localhost:3306/library",
  "dbUser": "librarian",
  "dbPassword": "I<3books"
}
```
Do not change the location of this config file or it's name. If you do, the program will end with an error.
Do not change the json keys. If you do, the program will end with an error.
### Database setup
Use the *database_setup/generation-script.sql* to create the database and it's tables on your MySQL server.
### Running the binary file
Double-click on the *library-database-manager.jar*.
If nothing happens, check if you have your Java added to PATH.
If you have Java in PATH and the program does not start, open your terminal (*Command Prompt* on *Windows*),  
navigate to the *library-database-manager/* and run this command:
```bash
javaw -jar library-database-manager.jar
```
## Contact
If you have any questions regarding this project, or you'd like to contribute, do not hesitate to contact me.
e-mail address: relich@post.cz
discord id: 697353234465030184
