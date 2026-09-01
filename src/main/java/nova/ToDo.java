package nova;

/**
 * A task with no date attached, such as "borrow book".
 * The simplest of the three types: it adds only a type tag to what Task
 * already provides.
 */
public class ToDo extends Task {

    /**
     * @param taskName what the user typed as the description
     * @param isMarked whether the task starts out done
     */
    public ToDo(String taskName, boolean isMarked) {
        super(taskName, isMarked);
    }

    /**
     * @return this task as one data-file line, e.g. "T | 0 | borrow book"
     */
    @Override
    public String toDataString() {
        return "T | " + dataFields();
    }

    /**
     * @return this task for display, e.g. "[T][ ] borrow book"
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
