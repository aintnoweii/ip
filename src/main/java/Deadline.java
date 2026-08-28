public class Deadline extends Task {
    protected final String by;

    public Deadline(String taskName, boolean isMarked, String by) {
        super(taskName, isMarked);
        this.by = by;
    }

    @Override
    protected String toDataString() {
        return "D | " + dataFields() + " | " + this.by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + String.format(" (by: %s)", this.by);
    }
}
