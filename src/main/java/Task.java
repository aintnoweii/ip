public abstract class Task {
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

    protected String dataFields() {
        return (isMarked ? "1" : "0") + " | " + this.taskName;
    }

    protected abstract String toDataString();

    @Override
    public String toString() {
        return (isMarked ? "[X]" : "[ ]") + " " + this.taskName;
    }
}
