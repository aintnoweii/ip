package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    private TaskList sampleList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book", true));
        tasks.add(new Deadline("return book", true, LocalDateTime.of(2019, 6, 6, 0, 0)));
        tasks.add(new ToDo("join sports club", false));
        return new TaskList(tasks);
    }

    @Test
    public void find_keywordInSeveralTasks_matchesInOriginalOrder() {
        TaskList matches = sampleList().find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][X] read book", matches.get(0).toString());
        assertEquals("[D][X] return book (by: Jun 06 2019)", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatches() {
        assertEquals(2, sampleList().find("BOOK").size());
        assertEquals(2, sampleList().find("Book").size());
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        assertTrue(sampleList().find("holiday").isEmpty());
    }

    @Test
    public void find_partialWord_matchesSubstring() {
        assertEquals(1, sampleList().find("sport").size());
    }

    @Test
    public void find_anyKeyword_leavesOriginalListUntouched() {
        TaskList original = sampleList();
        original.find("book");

        assertEquals(3, original.size());
    }
}
