package nova;

import java.time.LocalDateTime;

public class Deadline extends Task {
    protected final LocalDateTime by;

    /**
     * @param taskName what the user typed as the description.
     * @param isMarked whether the task starts out done.
     * @param by       when the task is due; a midnight time means the user
     *                 gave a date only, and no time is displayed.
     */
    public Deadline(String taskName, boolean isMarked, LocalDateTime by) {
        super(taskName, isMarked);
        this.by = by;
    }

    @Override
    public String toDataString() {
        // LocalDateTime.toString() writes ISO-8601, which LocalDateTime.parse reads back exactly.
        return "D | " + dataFields() + " | " + this.by;
    }

    /**
     * @return this task for display, e.g.
     *         "[D][ ] return book (by: Oct 15 2019, 6:00PM)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", formatDateTime(this.by));
    }
}
