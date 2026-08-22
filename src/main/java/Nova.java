import java.util.Scanner;
import java.util.ArrayList;

/**
 * Entry point for the Nova chatbot.
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

    private static void handleStatusCommand(String command, int index) {
        if (index <= 0 || index > tasks.size()) {
            printMessage("The number you have entered does not exist in your list. Try again!");
            return;
        }

        if (command.equals("mark")) {
            tasks.get(index - 1).mark();
            printMessage("Nice! I've marked this task as done:\n  " + tasks.get(index - 1).toString());
        } else if (command.equals("unmark")){
            tasks.get(index - 1).unmark();
            printMessage("OK, I've marked this task as not done yet:\n  " + tasks.get(index - 1).toString());
        }
    }


    private static void runLoop() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim(); // Removes leading and trailing whitespaces

            if (input.isEmpty()) { // Accounts for empty inputs so we don't get "empty" tasks in the arraylist
                printMessage("Type something!");
                continue;
            }

            String[] parts = input.split("/by|/from|/to");

            String[] task = parts[0].split("\\s+", 2);
            String command = task[0].toLowerCase();
            String argument = task.length > 1 ? task[1] : "";

            switch (command) {
                case "bye" -> {
                    printMessage(FAREWELL);
                    return;
                }
                case "list" -> {
                    if (tasks.isEmpty()) {
                        printMessage("Your list is empty! Add something.");
                        continue;
                    }
                    System.out.println(DIVIDER);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        String outString = String.format("%d.%s", i + 1, tasks.get(i).toString());
                        System.out.println(outString);
                    }
                    System.out.println(DIVIDER);
                }
                case "mark", "unmark" -> {
                    if (isInteger(argument)) {
                        handleStatusCommand(command, Integer.parseInt(argument));
                    } else {
                        printMessage("Invalid argument! Specify which task you wish to mark/unmark");
                    }
                }
                case "todo" -> {
                    tasks.add(new ToDo(argument));
                    printMessage("ToDo added");
                }
                case "deadline" -> {
                    if (parts.length == 2) {
                        tasks.add(new Deadline(argument, parts[1].trim()));
                        printMessage("Deadline added");
                    } else {
                        printMessage("Input your deadline");
                    }
                }
                case "event" -> {
                    if (parts.length == 3) {
                        tasks.add(new Event(argument, parts[1].trim(), parts[2].trim()));
                        printMessage("Event added");
                    } else {
                        printMessage("Input your start and end dates/times");
                    }
                }
                default -> {
                    printMessage("Input valid command - todo, deadline, event, mark or unmark");
                }
            }
        }
        printMessage(FAREWELL);
    }
}
