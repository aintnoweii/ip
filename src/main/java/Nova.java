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
    private static final ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        printMessage(BANNER + "\n" + GREETING);
        runLoop();
    }

    private static void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    private static boolean isValidStatusCommand(String input) {
        String[] splittedString = input.trim().split("\\s+");

        if (splittedString.length != 2) {
            return false;
        }

        return (splittedString[0].equalsIgnoreCase("mark")
                    || splittedString[0].equalsIgnoreCase("unmark")) && isInteger(splittedString[1]);
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int processCommand(String input) {
        String[] splittedString = input.split("\\s+");
        String arg1 = splittedString[0];
        int arg2 = Integer.parseInt(splittedString[1]);

        if (arg2 <= 0 || arg2 > tasks.size()) {
            return 1;
        }

        if (arg1.equalsIgnoreCase("mark")) {
            tasks.get(arg2 - 1).mark();
            printMessage("Nice! I've marked this task as done:\n" + tasks.get(arg2 - 1).toString());
            return 0;
        } else if (arg1.equalsIgnoreCase("unmark")){
            tasks.get(arg2 - 1).unmark();
            printMessage("OK, I've unmarked this task as undone:\n" + tasks.get(arg2 - 1).toString());
            return 0;
        }

        return 2;
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
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    String outString = String.format("%d. %s", i + 1, tasks.get(i).toString());
                    System.out.println(outString);
                }
                System.out.println(DIVIDER);
            } else if (isValidStatusCommand(input)) {
                int returnValue = processCommand(input);

                if (returnValue == 1) {
                    printMessage("The number you have entered does not exist in your list. Try again!");
                    continue;
                }
            } else {
                tasks.add(new Task(input));
                printMessage("added: " + input);
            }
        }
        printMessage(FAREWELL);
    }
}
