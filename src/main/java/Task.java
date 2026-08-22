public class Task {
    protected String taskName;
    protected boolean isMarked;

    public Task(String taskName) {
        this.taskName = taskName;
        this.isMarked = false;
    }

    public void mark() {
        this.isMarked = true;
    }

    public void unmark() {
        this.isMarked = false;
    }

    @Override
    public String toString() {
        return isMarked ? "[X]" : "[ ]";
    }
}
