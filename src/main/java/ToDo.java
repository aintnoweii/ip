public class ToDo extends Task {

    public ToDo(String taskName, boolean isMarked) {
        super(taskName, isMarked);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
