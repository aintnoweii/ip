package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void toString_todo_showsTypeAndStatus() {
        assertEquals("[T][ ] borrow book", new ToDo("borrow book", false).toString());
        assertEquals("[T][X] borrow book", new ToDo("borrow book", true).toString());
    }

    @Test
    public void toString_deadlineWithTime_showsFormattedDate() {
        Deadline d = new Deadline("return book", false, LocalDateTime.of(2019, 10, 15, 18, 0));
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00PM)", d.toString());
    }

    @Test
    public void toString_deadlineAtMidnight_omitsTime() {
        Deadline d = new Deadline("return book", false, LocalDateTime.of(2019, 10, 15, 0, 0));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", d.toString());
    }

    @Test
    public void toDataString_roundTripsThroughParser() {
        Deadline original = new Deadline("return book", true, LocalDateTime.of(2019, 10, 15, 18, 0));
        Task reloaded = Parser.parseDataLine(original.toDataString());
        assertEquals(original.toString(), reloaded.toString());
    }
}
