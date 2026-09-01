package nova;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Task {
    /** Display format used when the user gave a date but no time. */
    private static final DateTimeFormatter DATE_ONLY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Display format used when the user gave a date and a time. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected String taskName;
    protected boolean isMarked;

    public Task(String taskName, boolean isMarked) {
        this.taskName = taskName;
        this.isMarked = isMarked;
    }

    public void mark() {
        this.isMarked = true;
    }

    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Returns the description the user gave for this task.
     *
     * @return the task description.
     */
    public String getTaskName() {
        return this.taskName;
    }

    protected String dataFields() {
        return (isMarked ? "1" : "0") + " | " + this.taskName;
    }

    /**
     * Formats a date-time for display.
     * A LocalDateTime always carries a time, so midnight is taken to mean
     * "the user gave a date only" and the time is left out of the output.
     *
     * @param dateTime the value to render.
     * @return e.g. "Oct 15 2019" or "Oct 15 2019, 6:00PM".
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        boolean hasTime = !dateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
        return dateTime.format(hasTime ? DATE_TIME_FORMAT : DATE_ONLY_FORMAT);
    }

    public abstract String toDataString();

    @Override
    public String toString() {
        return (isMarked ? "[X]" : "[ ]") + " " + this.taskName;
    }
}
