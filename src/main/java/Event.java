public class Event extends Task {
    protected final String from;
    protected final String to;

    public Event(String taskName, boolean isMarked, String from, String to) {
        super(taskName, isMarked);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String toDataString() {
        return "E | " + dataFields() + " | " + this.from + " | " + this.to;
    }


    @Override
    public String toString() {
        return "[E]" + super.toString() + String.format(" (from: %s to: %s)", this.from, this.to);
    }
}
