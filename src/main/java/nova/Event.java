package nova;

import java.time.LocalDateTime;

public class Event extends Task {
    protected final LocalDateTime from;
    protected final LocalDateTime to;

    public Event(String taskName, boolean isMarked, LocalDateTime from, LocalDateTime to) {
        super(taskName, isMarked);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        // LocalDateTime.toString() writes ISO-8601, which LocalDateTime.parse reads back exactly.
        return "E | " + dataFields() + " | " + this.from + " | " + this.to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)",
                formatDateTime(this.from), formatDateTime(this.to));
    }
}
