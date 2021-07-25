package lessons.lesson_4_add_layers_and_factories.terminal_views;

import lessons.lesson_4_add_layers_and_factories.entities.users.UserEntity;
import lessons.lesson_4_add_layers_and_factories.services.users.UserService;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Scanner;

public class UserTerminal {
    private UserService userService;
    private Scanner scanner;
    private PrintWriter printer;

    public UserTerminal(UserService userService, PrintWriter printer, Scanner scanner) {
        this.userService = userService;
        this.printer = printer;
        this.scanner = scanner;
    }

    public UserEntity registerOrLogin() throws SQLException {
        printer.println("Do you have registered account at our Service?");
        printer.println("Type in Y for yes, N for no");
        UserEntity user = null;
        String answer = null;
        while (scanner.hasNext()) {
            answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("y")) {
                user = loginUser();
                break;
            } else if (answer.equalsIgnoreCase("n")) {
                user = registerUser();
                loginUser();
                break;
            } else if (answer.equalsIgnoreCase("exit")) {
                break;
            } else {
                printer.println("type in Y for yes, N for no or \"exit\" to quit");
            }
        }

        return user;
    }

    private UserEntity registerUser() throws SQLException {
        String name = getUsername();
        String password = getPassword();
        UserEntity newUser = new UserEntity(name, password);

        return userService.insert(newUser);
    }

    private UserEntity loginUser() throws SQLException {
        UserEntity user = null;
        String name = null;
        String password = null;

        while (user == null) {
            name = getUsername();
            password = getPassword();
            user = userService.findByUserNameAndPassword(name, password);
            if (user != null) {
                return user;
            }
            printer.println("Sorry, no such user in the database\n" +
                    "Please try again");
        }

        return null;
    }

    private String getUsername() {
        String question = "Please enter username";
        printer.println(question);
        String name = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                name = scanner.nextLine();
                if (!name.isEmpty()) {
                    break;
                } else {
                    printer.println(question);
                }
            }
        }

        return name;
    }

    private String getPassword() {
        String question = "Please enter password";
        printer.println(question);
        String password = null;
        while (scanner.hasNext()) {
            if (scanner.hasNextLine()) {
                password = scanner.nextLine();
                if (!password.isEmpty()) {
                    break;
                } else {
                    printer.println(question);
                }
            }
        }

        return password;
    }
}
