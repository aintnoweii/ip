package nova;

import java.time.LocalDateTime;

public class Deadline extends Task {
    protected final LocalDateTime by;

    public Deadline(String taskName, boolean isMarked, LocalDateTime by) {
        super(taskName, isMarked);
        this.by = by;
    }

    @Override
    public String toDataString() {
        // LocalDateTime.toString() writes ISO-8601, which LocalDateTime.parse reads back exactly.
        return "D | " + dataFields() + " | " + this.by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", formatDateTime(this.by));
    }
}
