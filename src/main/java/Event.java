public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String taskName, boolean isMarked, String from, String to) {
        super(taskName, isMarked);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)", this.from, this.to);
    }
}
