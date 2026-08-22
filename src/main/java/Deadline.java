public class Deadline extends Task {
    private final String by;

    public Deadline(String taskName, String by) {
        super(taskName);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " " + super.taskName + String.format("(by: %s)", this.by);
    }
}
