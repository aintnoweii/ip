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

    private Event eventFrom(String name, int startHour, int endHour, boolean isDone) {
        return new Event(name, isDone,
                LocalDateTime.of(2026, 1, 5, startHour, 0),
                LocalDateTime.of(2026, 1, 5, endHour, 0));
    }

    @Test
    public void findClashes_severalOverlappingEvents_returnsAllInListOrder() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("standup", 9, 10, false));
        tasks.add(eventFrom("interview", 11, 12, false));
        tasks.add(eventFrom("review", 9, 13, false));

        ArrayList<Event> clashes = new TaskList(tasks).findClashes(eventFrom("new", 9, 12, false));

        assertEquals(3, clashes.size());
        assertEquals("standup", clashes.get(0).getTaskName());
        assertEquals("interview", clashes.get(1).getTaskName());
        assertEquals("review", clashes.get(2).getTaskName());
    }

    @Test
    public void findClashes_noOverlap_returnsEmptyList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("standup", 9, 10, false));

        assertTrue(new TaskList(tasks).findClashes(eventFrom("new", 14, 15, false)).isEmpty());
    }

    @Test
    public void findClashes_overlappingDoneEvent_isSkipped() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("finished", 9, 12, true));

        assertTrue(new TaskList(tasks).findClashes(eventFrom("new", 10, 11, false)).isEmpty());
    }

    @Test
    public void findClashes_deadlinesAndTodos_areIgnored() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("read book", false));
        tasks.add(new Deadline("report", false, LocalDateTime.of(2026, 1, 5, 10, 0)));

        assertTrue(new TaskList(tasks).findClashes(eventFrom("new", 9, 12, false)).isEmpty());
    }

    @Test
    public void countClashingPairs_noClashes_returnsZero() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("a", 9, 10, false));
        tasks.add(eventFrom("b", 10, 11, false));

        assertEquals(0, new TaskList(tasks).countClashingPairs());
    }

    @Test
    public void countClashingPairs_onePair_returnsOne() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("a", 9, 11, false));
        tasks.add(eventFrom("b", 10, 12, false));
        tasks.add(eventFrom("c", 14, 15, false));

        assertEquals(1, new TaskList(tasks).countClashingPairs());
    }

    @Test
    public void countClashingPairs_threeMutuallyOverlapping_returnsThree() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("a", 9, 12, false));
        tasks.add(eventFrom("b", 10, 13, false));
        tasks.add(eventFrom("c", 11, 14, false));

        assertEquals(3, new TaskList(tasks).countClashingPairs());
    }

    @Test
    public void countClashingPairs_doneEventsExcluded_returnsZero() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(eventFrom("a", 9, 12, true));
        tasks.add(eventFrom("b", 10, 13, false));

        assertEquals(0, new TaskList(tasks).countClashingPairs());
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
