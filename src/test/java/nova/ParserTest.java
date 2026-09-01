package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parseDateTime_dateOnly_timeIsMidnight() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                Parser.parseDateTime("2019-10-15"));
    }

    @Test
    public void parseDateTime_dateAndTime_bothKept() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseDateTime("2019-10-15 1800"));
    }

    @Test
    public void parseDateTime_unrecognisedText_returnsNull() {
        assertNull(Parser.parseDateTime("Sunday"));
        assertNull(Parser.parseDateTime("2pm"));
        assertNull(Parser.parseDateTime("2019-1-5"));
    }

    @Test
    public void parseDateTime_impossibleDate_returnsNull() {
        assertNull(Parser.parseDateTime("2019-02-30 1800"));
        assertNull(Parser.parseDateTime("2019-04-31 1800"));
    }
}
