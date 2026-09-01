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

    /**
     * Starts the chatbot, reading commands from standard input until the user
     * says goodbye.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Nova(DATA_FILE_PATH).run();
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
     * Writes the current tasks to disk, reporting any failure to the user.
     * Every command that changes the list saves through here, so the error
     * handling and the "did the load succeed" check live in one place.
     */
    private void save() {
        if (!canSave) {
            return;
        }

        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.printMessage("Error. Could not save your tasks: " + e.getMessage());
        }
    }

    /**
     * Renders the task list as one framed block.
     */
    private void showTaskList() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            lines.add(String.format("%d.%s", i + 1, tasks.get(i)));
        }

        ui.printMessage(String.join("\n", lines));
    }

    private void runLoop() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim(); // Removes leading and trailing whitespaces

            if (input.isEmpty()) { // Accounts for empty inputs so we don't get "empty" tasks in the arraylist
                ui.printMessage("Type something!");
                continue;
            }

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            String argument = parts.length > 1 ? parts[1].trim() : "";

            switch (command) {
                case "bye" -> {
                    ui.showFarewell();
                    return;
                }
                case "list" -> {
                    if (tasks.isEmpty()) {
                        ui.printMessage("Your list is empty! Add something.");
                        continue;
                    }
                    showTaskList();
                }
                case "mark", "unmark" -> {
                    if (!Parser.isInteger(argument)) {
                        ui.printMessage("Invalid argument! Specify which task you wish to mark/unmark");
                        continue;
                    }

                    int index = Integer.parseInt(argument) - 1; // the user counts from 1
                    if (index < 0 || index >= tasks.size()) {
                        ui.printMessage("The number you have entered does not exist in your list."
                                + " Try again!");
                        continue;
                    }

                    boolean isMarking = command.equals("mark");
                    Task updated = isMarking ? tasks.mark(index) : tasks.unmark(index);
                    ui.showTaskMarked(updated, isMarking);
                    save();
                }
                case "delete" -> {
                    if (argument.isBlank()) {
                        ui.printMessage("Pls specify which task to delete!");
                        continue;
                    }

                    if (!Parser.isInteger(argument)) {
                        ui.printMessage("Specify a number after the command delete");
                        continue;
                    }

                    int index = Integer.parseInt(argument) - 1; // the user counts from 1
                    if (index < 0 || index >= tasks.size()) {
                        ui.printMessage("Specify a valid task number!");
                        continue;
                    }

                    Task removed = tasks.remove(index);
                    ui.showTaskRemoved(removed, tasks.size());
                    save();
                }
                case "todo" -> {
                    if (argument.isBlank()) {
                        ui.printMessage("Pls specify your to-do task after the command todo!");
                        continue;
                    }
                    ToDo latestToDo = new ToDo(argument, false);
                    tasks.add(latestToDo);
                    save();
                    ui.showTaskAdded(latestToDo, tasks.size());
                }
                case "deadline" -> {
                    String[] b = argument.split("/by", 2);
                    if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                        ui.printMessage("Use: deadline <task name> /by <end>");
                        continue;
                    }
                    LocalDateTime by = Parser.parseDateTime(b[1].trim());
                    if (by == null) {
                        ui.printMessage("I couldn't understand that date. " + Parser.DATE_FORMAT_HINT);
                        continue;
                    }

                    Deadline latestDeadline = new Deadline(b[0].trim(), false, by);
                    tasks.add(latestDeadline);
                    save();
                    ui.showTaskAdded(latestDeadline, tasks.size());
                }
                case "event" -> {
                    String[] f = argument.split("/from", 2);
                    String[] t = f.length > 1 ? f[1].split("/to", 2) : new String[0];

                    if (f.length < 2 || t.length < 2 || f[0].isBlank() || t[0].isBlank() || t[1].isBlank()) {
                        ui.printMessage("Use: event <task name> /from <start> /to <end>");
                        continue;
                    }
                    LocalDateTime from = Parser.parseDateTime(t[0].trim());
                    LocalDateTime to = Parser.parseDateTime(t[1].trim());
                    if (from == null || to == null) {
                        ui.printMessage("I couldn't understand that date. " + Parser.DATE_FORMAT_HINT);
                        continue;
                    }

                    Event latestEvent = new Event(f[0].trim(), false, from, to);
                    tasks.add(latestEvent);
                    save();
                    ui.showTaskAdded(latestEvent, tasks.size());
                }
                default -> {
                    ui.printMessage("Input valid command - start with todo, deadline, event, mark or unmark");
                }
            }
        }
        ui.showFarewell();
    }
}
