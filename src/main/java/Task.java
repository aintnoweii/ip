public class Task {
    private String taskName;
    private boolean isMarked;

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
        String checkBox = isMarked ? "[X]" : "[ ]";
        return checkBox + " " + this.taskName;
    }
}
