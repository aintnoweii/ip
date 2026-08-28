import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    /** Location of the file tasks are persisted to, relative to the working directory. */
    private static final String DATA_FILE_PATH = "data/nova.txt";

    /** Input format accepted when the user supplies a date and a time. */
    private static final DateTimeFormatter INPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** Shown whenever a date the user typed could not be understood. */
    private static final String DATE_FORMAT_HINT =
            "Dates must look like 2019-10-15 or 2019-10-15 1800.";

    // Array for storing items
    private static final ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        printMessage(BANNER + "\n" + GREETING);
        retrieveData();
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

    private static void printTaskAddition(Task t) {
        printMessage("Got it. I've added this task:\n" + "  " + t.toString() + "\n"
                + String.format("Now you have %d tasks in the list.", tasks.size()));
    }

    private static void handleDelete(int numberToDelete) {
        Task toBeDeleted = tasks.get(numberToDelete - 1);
        tasks.remove(numberToDelete - 1);
        printMessage("Noted, I've removed this task:\n  " + toBeDeleted.toString() + "\n"
                + String.format("Now you have %d tasks in the list", tasks.size()));
    }

    /**
     * Turns a date the user typed into a LocalDateTime.
     * Accepts "yyyy-MM-dd HHmm", or "yyyy-MM-dd" on its own, in which case the
     * time becomes midnight and is later left out of the display.
     * Returns null rather than throwing so callers can report and move on,
     * matching the convention used by {@link #parseDataLine(String)}.
     *
     * @param rawDate the text between the command markers, already trimmed
     * @return the parsed value, or null if it matched neither format
     */
    private static LocalDateTime parseDateTime(String rawDate) {
        try {
            return LocalDateTime.parse(rawDate, INPUT_WITH_TIME);
        } catch (DateTimeParseException e) {
            // Not a date-and-time; fall through and try a bare date.
        }

        try {
            return LocalDate.parse(rawDate).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Rebuilds a single task from one line of the data file.
     * Returning null rather than throwing lets the caller skip a damaged
     * line and keep loading the rest of the file.
     *
     * @param dataLine one saved line, e.g. "D | 0 | return book | Sunday"
     * @return the reconstructed task, or null if the line is malformed
     */
    private static Task parseDataLine(String dataLine) {
        String[] dataLineComponents = dataLine.split("\\|");

        // Every task type needs at least a type, a done flag and a description.
        if (dataLineComponents.length < 3) {
            return null;
        }

        String markedField = dataLineComponents[1].trim();
        if (!isInteger(markedField)) {
            return null;
        }

        String typeOfTask = dataLineComponents[0].trim();
        boolean isMarked = Integer.parseInt(markedField) == 1;
        String taskStored = dataLineComponents[2].trim();

        if (taskStored.isEmpty()) {
            return null;
        }

        switch (typeOfTask) {
            case "T":
                return new ToDo(taskStored, isMarked);
            case "D":
                if (dataLineComponents.length < 4) {
                    return null;
                }
                LocalDateTime by = parseStoredDateTime(dataLineComponents[3]);
                if (by == null) {
                    return null;
                }
                return new Deadline(taskStored, isMarked, by);
            case "E":
                if (dataLineComponents.length < 5) {
                    return null;
                }
                LocalDateTime from = parseStoredDateTime(dataLineComponents[3]);
                LocalDateTime to = parseStoredDateTime(dataLineComponents[4]);
                if (from == null || to == null) {
                    return null;
                }
                return new Event(taskStored, isMarked, from, to);
            default:
                return null;
        }
    }

    /**
     * Reads back a date-time written by {@code toDataString()}, which stores
     * values in ISO-8601. Lines saved before dates were typed hold free text
     * such as "2pm" and are rejected here, so the caller skips and counts them.
     *
     * @param storedField one date field from the data file
     * @return the parsed value, or null if the field is not valid ISO-8601
     */
    private static LocalDateTime parseStoredDateTime(String storedField) {
        try {
            return LocalDateTime.parse(storedField.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Reads the data file and fills up the arraylist.
     * A missing file means this is the first run, which is normal and reported
     * silently. Lines that cannot be parsed are skipped and counted, so one
     * damaged entry never stops the remaining tasks from loading.
     */
    private static void retrieveData() {
        File dataFile = new File(DATA_FILE_PATH);

        if (!dataFile.exists()) {
            return;
        }

        int skippedLines = 0;

        try (Scanner scanner = new Scanner(dataFile)) {
            while (scanner.hasNextLine()) {
                String dataLine = scanner.nextLine();

                if (dataLine.isBlank()) {
                    continue;
                }

                Task savedTask = parseDataLine(dataLine);
                if (savedTask == null) {
                    skippedLines++;
                } else {
                    tasks.add(savedTask);
                }
            }
        } catch (FileNotFoundException e) {
            // The file exists but could not be opened, e.g. no read permission.
            printMessage("Could not read your saved tasks: " + e.getMessage());
            return;
        }

        if (skippedLines > 0) {
            printMessage(String.format("Skipped %d unreadable line(s) in %s.",
                    skippedLines, DATA_FILE_PATH));
        }
    }

    /**
     * Rewrites the data file so that it matches the current task list.
     * The parent directory is created first because FileWriter creates a
     * missing file but never a missing directory.
     */
    private static void saveTasks() {
        File dataFile = new File(DATA_FILE_PATH);
        File dataDirectory = dataFile.getParentFile();

        // getParentFile() is null when the path has no directory part.
        if (dataDirectory != null) {
            dataDirectory.mkdirs();
        }

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(dataFile))) {
            for (Task t : tasks) {
                fileWriter.write(t.toDataString());
                fileWriter.newLine();
            }
        } catch (IOException e) {
            printMessage("Error. Could not save your tasks: " + e.getMessage());
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

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            String argument = parts.length > 1 ? parts[1].trim() : "";

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
                        saveTasks();
                    } else {
                        printMessage("Invalid argument! Specify which task you wish to mark/unmark");
                    }
                }
                case "delete" -> {
                    if (argument.isBlank()) {
                        printMessage("Pls specify which task to delete!");
                        continue;
                    }

                    if (!isInteger(argument)) {
                        printMessage("Specify a number after the command delete");
                        continue;
                    }

                    int numToDelete = Integer.parseInt(argument);
                    if (numToDelete <= 0 || numToDelete > tasks.size()) {
                        printMessage("Specify a valid task number!");
                        continue;
                    }

                    handleDelete(Integer.parseInt(argument));
                    saveTasks();
                }
                case "todo" -> {
                    if (argument.isBlank()) {
                        printMessage("Pls specify your to-do task after the command todo!");
                        continue;
                    }
                    ToDo latestToDo = new ToDo(argument, false);
                    tasks.add(latestToDo);
                    saveTasks();
                    printTaskAddition(latestToDo);
                }
                case "deadline" -> {
                    String[] b = argument.split("/by", 2);
                    if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                        printMessage("Use: deadline <task name> /by <end>");
                        continue;
                    }
                    LocalDateTime by = parseDateTime(b[1].trim());
                    if (by == null) {
                        printMessage("I couldn't understand that date. " + DATE_FORMAT_HINT);
                        continue;
                    }

                    Deadline latestDeadline = new Deadline(b[0].trim(), false, by);
                    tasks.add(latestDeadline);
                    saveTasks();
                    printTaskAddition(latestDeadline);
                }
                case "event" -> {
                    String[] f = argument.split("/from", 2);
                    String[] t = f.length > 1 ? f[1].split("/to", 2) : new String[0];

                    if (f.length < 2 || t.length < 2 || f[0].isBlank() || t[0].isBlank() || t[1].isBlank()) {
                        printMessage("Use: event <task name> /from <start> /to <end>");
                        continue;
                    }
                    LocalDateTime from = parseDateTime(t[0].trim());
                    LocalDateTime to = parseDateTime(t[1].trim());
                    if (from == null || to == null) {
                        printMessage("I couldn't understand that date. " + DATE_FORMAT_HINT);
                        continue;
                    }

                    Event latestEvent = new Event(f[0].trim(), false, from, to);
                    tasks.add(latestEvent);
                    saveTasks();
                    printTaskAddition(latestEvent);
                }
                default -> {
                    printMessage("Input valid command - start with todo, deadline, event, mark or unmark");
                }
            }
        }
        printMessage(FAREWELL);
    }
}
