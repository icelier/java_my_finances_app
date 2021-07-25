package lessons.lesson_4_add_layers_and_factories.terminal_views;

import java.io.PrintWriter;
import java.util.Scanner;

public class UtilTerminal {
    private Scanner scanner;
    private PrintWriter printer;

    public UtilTerminal(PrintWriter printer, Scanner scanner) {
        this.printer = printer;
        this.scanner = scanner;
    }

    public int chooseAndValidate(int validationCount) {
        int choice = -1;
        boolean choiceValidated = false;
        while (!choiceValidated) {
            printer.println("Please choose from the options above");

            choice = getUserChoice();
            choiceValidated = validateChoice(choice, validationCount);
            if (choiceValidated) {
                break;
            }
        }

        return choice;
    }

    public int getUserChoice() {
        int choice = -1;
        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                break;
            }
        }
        return choice;
    }

    public boolean validateChoice(int choice, int optionsCount) {
        return (choice >= 1 && choice <= optionsCount);
    }
}
