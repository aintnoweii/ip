import java.util.Scanner;
import java.util.ArrayList;

/**
 * Entry point for the Nova chatbot.
 * At present it only prints the startup banner.
 */
public class Nova {
    private static final String NAME = "Nova";
    private static final String BANNER = " _   _                      \n"
            + "| \\ | |  ___           __ _ \n"
            + "|  \\| | / _ \\ __   __ / _` |\n"
            + "| |\\  || (_) |\\ \\ / /| (_| |\n"
            + "|_| \\_| \\___/  \\ V /  \\__,_|\n"
            + "                \\_/         ";
    private static final String DIVIDER = "_".repeat(60);
    private static final String GREETING = String.format("Hello! I'm %s.\nWhat can I do for you?", NAME);
    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    // Array for storing items
    private static final ArrayList<String> tasks = new ArrayList<>();

    public static void main(String[] args) {
        printMessage(BANNER + "\n" + GREETING);
        runLoop();
    }

    private static void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    private static void runLoop() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim(); // Removes leading and trailing whitespaces

            if (input.isEmpty()) { // Accounts for empty inputs so we don't get "empty" tasks in the arraylist
                printMessage("Type something!");
                continue;
            } else if (input.equalsIgnoreCase("bye")) { // Ignores letter casing
                printMessage(FAREWELL);
                return;
            } else if (input.equalsIgnoreCase("list")) {
                if (tasks.isEmpty()) {
                    printMessage("Your list is empty! Add something.");
                    continue;
                }
                System.out.println(DIVIDER);
                for (int i = 0; i < tasks.size(); i++) {
                    String outString = String.format("%d. %s", i + 1, tasks.get(i));
                    System.out.println(outString);
                }
                System.out.println(DIVIDER);
            } else {
                tasks.add(input);
                printMessage("added: " + input);
            }
        }
        printMessage(FAREWELL);
    }
}
