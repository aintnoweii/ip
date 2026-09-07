package nova;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Nova chatbot.
 * A Nova instance owns the three collaborators it needs — a Ui to talk to the
 * user, a Storage to persist tasks, and a TaskList holding them — so command
 * handling reads them as fields rather than passing them from method to method.
 *
 * <p>Two interfaces sit on top of the same {@link #processCommand(String)}:
 * {@link #run()} for the console, and {@link #getResponse(String)} for the
 * JavaFX GUI. Both turn one line of input into one line of response text;
 * only how that text is shown differs.
 */
public class Nova {
    /** Location of the file tasks are persisted to, relative to the working directory. */
    private static final String DATA_FILE_PATH = "data/nova.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * False when the saved file exists but could not be read. Saving is then
     * refused, so a file we failed to load is never overwritten with an empty list.
     */
    private final boolean canSave;

    /** Set when loading failed, so run() can report it after the greeting. Null otherwise. */
    private final String loadError;

    /**
     * Creates a chatbot backed by the given data file, loading any saved tasks.
     * A missing file simply means an empty list. A file that exists but cannot
     * be read disables saving for the session, so the unreadable file is never
     * overwritten. Nothing is printed here; run() reports it in the right order.
     *
     * @param filePath where tasks are loaded from and saved to.
     */
    public Nova(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loadedTasks;
        String error;

        try {
            loadedTasks = new TaskList(storage.load());
            error = null;
        } catch (FileNotFoundException e) {
            loadedTasks = new TaskList(new ArrayList<>());
            error = "Could not read your saved tasks: " + e.getMessage()
                    + "\nStarting with an empty list, and saving is off so the file is left alone.";
        }

        this.tasks = loadedTasks;
        this.loadError = error;
        this.canSave = (error == null);
    }

    /** Creates a chatbot backed by the default save file. */
    public Nova() {
        this(DATA_FILE_PATH);
    }

    /**
     * Starts the chatbot, reading commands from standard input until the user
     * says goodbye.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Nova().run();
    }

    /**
     * Greets the user, reports any unreadable saved lines, then handles
     * commands until the user says goodbye or the input runs out.
     */
    public void run() {
        ui.showWelcome();

        if (loadError != null) {
            ui.printMessage(loadError);
        }

        if (storage.getSkippedLineCount() > 0) {
            ui.printMessage(String.format("Skipped %d unreadable line(s) in %s.",
                    storage.getSkippedLineCount(), storage.getFilePath()));
        }

        runLoop();
    }

    /**
     * Returns Nova's response to one line of input, for the GUI. Unlike the
     * console, the GUI has no separate exit step: "bye" simply returns its
     * farewell text like any other command, and the window stays open.
     *
     * @param input line of input entered by the user.
     * @return Nova's response.
     */
    public String getResponse(String input) {
        return processCommand(input.trim());
    }

    /**
     * Returns the greeting shown when the GUI starts, with no ASCII banner.
     *
     * @return the greeting text.
     */
    public String getGreeting() {
        return ui.getGreetingMessage();
    }

    /**
     * Writes the current tasks to disk.
     * Every command that changes the list saves through here, so the error
     * handling and the "did the load succeed" check live in one place.
     *
     * @return null on success, or an error message to show the user.
     */
    private String save() {
        if (!canSave) {
            return null;
        }

        try {
            storage.save(tasks);
            return null;
        } catch (IOException e) {
            return "Error. Could not save your tasks: " + e.getMessage();
        }
    }

    /**
     * Builds a list of tasks as one block of text under the given heading.
     * Numbering restarts at 1 for whatever is shown, so a filtered result
     * reads as its own list rather than exposing positions in the full one.
     *
     * @param taskList the tasks to display.
     * @param heading  the line shown above them.
     * @return the block of text.
     */
    private String buildTaskListMessage(TaskList taskList, String heading) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(heading);

        for (int i = 0; i < taskList.size(); i++) {
            lines.add(String.format("%d.%s", i + 1, taskList.get(i)));
        }

        return String.join("\n", lines);
    }

    /**
     * Returns whether a line of input is the command to exit the console
     * loop. Only run() uses this; getResponse() has no loop to exit.
     *
     * @param input line of input entered by the user.
     * @return true if the command word is "bye", regardless of case.
     */
    private static boolean isExitCommand(String input) {
        return input.split("\\s+", 2)[0].equalsIgnoreCase("bye");
    }

    /**
     * Appends a save-failure message to a response, if saving failed.
     *
     * @param message   the response built so far.
     * @param saveError null on success, or the error save() returned.
     * @return the combined response.
     */
    private static String withSaveResult(String message, String saveError) {
        return saveError == null ? message : message + "\n" + saveError;
    }

    /**
     * Turns one line of input into Nova's response text. This is the single
     * place command dispatch happens; both run() and getResponse() call it,
     * so the console and the GUI can never disagree about what a command does.
     *
     * @param input one line of input, already trimmed.
     * @return the response text.
     */
    private String processCommand(String input) {
        if (input.isEmpty()) { // Accounts for empty inputs so we don't get "empty" tasks in the arraylist
            return "Type something!";
        }

        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "bye" -> {
                return ui.getFarewellMessage();
            }
            case "list" -> {
                if (tasks.isEmpty()) {
                    return "Your list is empty! Add something.";
                }
                return buildTaskListMessage(tasks, "Here are the tasks in your list:");
            }
            case "find" -> {
                if (argument.isBlank()) {
                    return "Pls specify a keyword to search for, e.g. find book";
                }

                TaskList matches = tasks.find(argument);
                if (matches.isEmpty()) {
                    return "No tasks in your list mention \"" + argument + "\".";
                }

                return buildTaskListMessage(matches, "Here are the matching tasks in your list:");
            }
            case "mark", "unmark" -> {
                if (!Parser.isInteger(argument)) {
                    return "Invalid argument! Specify which task you wish to mark/unmark";
                }

                int index = Integer.parseInt(argument) - 1; // the user counts from 1
                if (index < 0 || index >= tasks.size()) {
                    return "The number you have entered does not exist in your list."
                            + " Try again!";
                }

                boolean isMarking = command.equals("mark");
                Task updated = isMarking ? tasks.mark(index) : tasks.unmark(index);
                return withSaveResult(ui.getTaskMarkedMessage(updated, isMarking), save());
            }
            case "delete" -> {
                if (argument.isBlank()) {
                    return "Pls specify which task to delete!";
                }

                if (!Parser.isInteger(argument)) {
                    return "Specify a number after the command delete";
                }

                int index = Integer.parseInt(argument) - 1; // the user counts from 1
                if (index < 0 || index >= tasks.size()) {
                    return "Specify a valid task number!";
                }

                Task removed = tasks.remove(index);
                return withSaveResult(ui.getTaskRemovedMessage(removed, tasks.size()), save());
            }
            case "todo" -> {
                if (argument.isBlank()) {
                    return "Pls specify your to-do task after the command todo!";
                }
                ToDo latestToDo = new ToDo(argument, false);
                tasks.add(latestToDo);
                return withSaveResult(ui.getTaskAddedMessage(latestToDo, tasks.size()), save());
            }
            case "deadline" -> {
                String[] b = argument.split("/by", 2);
                if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                    return "Use: deadline <task name> /by <end>";
                }
                LocalDateTime by = Parser.parseDateTime(b[1].trim());
                if (by == null) {
                    return "I couldn't understand that date. " + Parser.DATE_FORMAT_HINT;
                }

                Deadline latestDeadline = new Deadline(b[0].trim(), false, by);
                tasks.add(latestDeadline);
                return withSaveResult(ui.getTaskAddedMessage(latestDeadline, tasks.size()), save());
            }
            case "event" -> {
                String[] f = argument.split("/from", 2);
                String[] t = f.length > 1 ? f[1].split("/to", 2) : new String[0];

                if (f.length < 2 || t.length < 2 || f[0].isBlank() || t[0].isBlank() || t[1].isBlank()) {
                    return "Use: event <task name> /from <start> /to <end>";
                }
                LocalDateTime from = Parser.parseDateTime(t[0].trim());
                LocalDateTime to = Parser.parseDateTime(t[1].trim());
                if (from == null || to == null) {
                    return "I couldn't understand that date. " + Parser.DATE_FORMAT_HINT;
                }

                Event latestEvent = new Event(f[0].trim(), false, from, to);
                tasks.add(latestEvent);
                return withSaveResult(ui.getTaskAddedMessage(latestEvent, tasks.size()), save());
            }
            default -> {
                return "Input valid command - start with todo, deadline, event,"
                        + " list, find, mark, unmark or delete";
            }
        }
    }

    private void runLoop() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim(); // Removes leading and trailing whitespaces
            ui.printMessage(processCommand(input));

            if (isExitCommand(input)) {
                return;
            }
        }
        ui.printMessage(ui.getFarewellMessage());
    }
}
