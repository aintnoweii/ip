package nova;

import java.time.LocalDateTime;

/**
 * A task that runs between two points in time, such as
 * "project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)".
 * Both ends are stored as LocalDateTime values rather than text, so the format
 * the user types is independent of the format shown back to them.
 */
public class Event extends Task {
    protected final LocalDateTime from;
    protected final LocalDateTime to;

    /**
     * @param taskName what the user typed as the description
     * @param isMarked whether the task starts out done
     * @param from     when the event starts
     * @param to       when the event ends
     */
    public Event(String taskName, boolean isMarked, LocalDateTime from, LocalDateTime to) {
        super(taskName, isMarked);
        this.from = from;
        this.to = to;
    }

    /**
     * @return this task as one data-file line, e.g.
     *         "E | 0 | meeting | 2019-10-15T14:00 | 2019-10-15T16:00". Dates are
     *         written in ISO-8601 so that they read back exactly.
     */
    @Override
    public String toDataString() {
        return "E | " + dataFields() + " | " + this.from + " | " + this.to;
    }

    /**
     * @return this task for display, e.g.
     *         "[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)"
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)",
                formatDateTime(this.from), formatDateTime(this.to));
    }
}
