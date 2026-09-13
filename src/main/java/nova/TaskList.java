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
        // Storage.load() promises never to return null, and the only other
        // caller passes a fresh list. A null here would surface much later,
        // as an NPE inside some unrelated command.
        assert tasks != null : "TaskList constructed with a null list";

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
        assert isInRange(index) : "get() index out of range: " + index + " of " + size();

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
        assert isInRange(index) : "remove() index out of range: " + index + " of " + size();

        return this.tasks.remove(index);
    }

    /**
     * Marks the task at the given position as done.
     *
     * @param index 0-based position of the task.
     * @return the task that was marked, for the caller to display.
     */
    Task mark(int index) {
        assert isInRange(index) : "mark() index out of range: " + index + " of " + size();

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
        assert isInRange(index) : "unmark() index out of range: " + index + " of " + size();

        Task task = this.tasks.get(index);
        task.unmark();
        return task;
    }

    /**
     * Reports whether a position is a valid index into this list.
     * The commands validate the number the user typed and report a friendly
     * message if it is wrong, so by the time a position reaches this class it
     * has already been checked. This backs the assertions that record that
     * contract; it is not a substitute for the caller's own validation.
     *
     * @param index 0-based position to test.
     * @return true if a task exists at that position.
     */
    private boolean isInRange(int index) {
        return index >= 0 && index < this.tasks.size();
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     * The comparison ignores case, because someone searching for "Book" is
     * looking for the same thing as someone searching for "book"; a
     * case-sensitive search would miss more than it caught.
     * The result is a separate list, so filtering never disturbs the real one.
     *
     * @param keyword text to look for inside each task description.
     * @return the matching tasks, in the order they appear in this list.
     */
    TaskList find(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();

        for (Task task : this.tasks) {
            if (task.getTaskName().toLowerCase().contains(lowerCaseKeyword)) {
                matches.add(task);
            }
        }

        return new TaskList(matches);
    }

}
