# Library database manager
## Program's purpose
This application serves for managing a librarz database. It connects to a MySQL database and let's you view, add, update and delete data such as info about books, readers, loans, etc.

## How it works
Configuration
The app loads credentials from config.json, which is located in conf/ The content of the file may look something like this:
```json
{
  "dbUrl": "jdbc:mysql://domain|ip:3306/library",
  "dbUser": "yourUsername",
  "dbPassword": "yourPassword"
}
```
Do not change the location of this config file or it's name.
You may change the values of the json body, but do not change the keys. If you do, the program will end with an error.

## Controls

Requirements
Code editor, which can run C# code (Jetbrains Rider, Visual Studio, Visual Studio Code, ...)
.Net SDK 8.0
How to run program
Open your code editor.
Run the Main method in file Program.cs
Alternatively, you can go to /bin/Debug/net8.0/ and run the PasswordCracker binary file. This method does not need the requirements above.
If you are using release option, go to the /win-x64/ and run dictionary-attack.exe
Legal disclaimer
The program is not intended for illegal activity. The author is not responsible for any legal issues.
Contact
If you have any questions regarding this project, or you'd like to contribute, do not hesitate to contact me.
e-mail address: relich@post.cz
discord id: 697353234465030184
