/**
 * Handles everything the user sees.
 * All console output goes through here, so the rest of the program never
 * calls System.out directly and could be moved to another interface by
 * replacing only this class.
 */
public class Ui {
    private static final String DIVIDER = "_".repeat(60);

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Nova";

    /** ASCII-art logo shown once at startup. */
    private static final String BANNER = " _   _                      \n"
            + "| \\ | |  ___           __ _ \n"
            + "|  \\| | / _ \\ __   __ / _` |\n"
            + "| |\\  || (_) |\\ \\ / /| (_| |\n"
            + "|_| \\_| \\___/  \\ V /  \\__,_|\n"
            + "                \\_/         ";

    private static final String GREETING =
            String.format("Hello! I'm %s.\nWhat can I do for you?", NAME);

    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    /**
     * Shows the startup banner and greeting as one block.
     */
    void showWelcome() {
        printMessage(BANNER + "\n" + GREETING);
    }

    /**
     * Says goodbye. Shown both when the user types "bye" and when input runs out.
     */
    void showFarewell() {
        printMessage(FAREWELL);
    }

    /**
     * Prints one block of output, framed by a divider above and below.
     * Routing every message through here means no caller can produce a
     * half-framed block.
     *
     * @param message text to show, may span several lines
     */
    void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /**
     * Reports that a task was added to the list.
     *
     * @param task      the task just added
     * @param taskCount how many tasks the list now holds
     */
    void showTaskAdded(Task task, int taskCount) {
        printMessage("Got it. I've added this task:\n  " + task + "\n"
                + String.format("Now you have %d task%s in the list.",
                taskCount, taskCount == 1 ? "" : "s"));
    }

    /**
     * Reports that a task was deleted from the list.
     *
     * @param task      the task just removed
     * @param taskCount how many tasks remain
     */
    void showTaskRemoved(Task task, int taskCount) {
        printMessage("Noted, I've removed this task:\n  " + task + "\n"
                + String.format("Now you have %d task%s in the list.",
                taskCount, taskCount == 1 ? "" : "s"));
    }

    /**
     * Reports that a task's done status changed.
     *
     * @param task     the task that was updated
     * @param isMarked true if it was marked done, false if it was un-marked
     */
    void showTaskMarked(Task task, boolean isMarked) {
        String heading = isMarked
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        printMessage(heading + "\n  " + task);
    }
}
