package nova;

import java.util.ArrayList;

/**
 * The user's tasks, in the order they were added.
 * This class wraps an ArrayList rather than extending it, so callers see only
 * the operations the commands actually need and cannot bypass them.
 * Indices here are 0-based like the underlying list; the 1-based numbers the
 * user types are converted before they reach this class.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates a list holding the given tasks, typically the ones just loaded
     * from disk. The list is used directly rather than copied.
     *
     * @param tasks initial contents, empty for a fresh start.
     */
    TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Reports whether the list holds no tasks.
     *
     * @return true if there are no tasks.
     */
    boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks.
     */
    int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at the given position.
     *
     * @param index 0-based position of the task.
     * @return the task at that position.
     */
    Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task the task to add.
     */
    void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Deletes the task at the given position.
     * The removed task is returned so the caller can report what went, without
     * having to look it up beforehand.
     *
     * @param index 0-based position of the task.
     * @return the task that was removed.
     */
    Task remove(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Marks the task at the given position as done.
     *
     * @param index 0-based position of the task.
     * @return the task that was marked, for the caller to display.
     */
    Task mark(int index) {
        Task task = this.tasks.get(index);
        task.mark();
        return task;
    }

    /**
     * Marks the task at the given position as not done.
     *
     * @param index 0-based position of the task.
     * @return the task that was un-marked, for the caller to display.
     */
    Task unmark(int index) {
        Task task = this.tasks.get(index);
        task.unmark();
        return task;
    }

}
