package lessons.lesson_4_add_layers_and_factories.singleton_factories;

import java.io.PrintWriter;
import java.util.Scanner;

public class UtilsFactory {
    private static Scanner scanner;
    private static PrintWriter printer;

    public static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }

        return scanner;
    }

    public static PrintWriter getPrinter() {
        if (printer == null) {
            printer = new PrintWriter(System.out, true);
        }

        return printer;
    }
}
