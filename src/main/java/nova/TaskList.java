package nova;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    int size() {
        return this.tasks.size();
    }

    Task get(int i) {
        return this.tasks.get(i);
    }

    void add(Task task) {
        this.tasks.add(task);
    }

    Task remove(int i) {
        return this.tasks.remove(i);
    }

    Task mark(int i) {
        Task task = this.tasks.get(i);
        task.mark();
        return task;
    }

    Task unmark(int i) {
        Task task = this.tasks.get(i);
        task.unmark();
        return task;
    }

}
