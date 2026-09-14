package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parseDataLine_backwardsEvent_returnsNull() {
        assertNull(Parser.parseDataLine("E | 0 | oops | 2026-01-06T00:00 | 2026-01-05T00:00"));
    }

    @Test
    public void parseDataLine_zeroLengthEvent_isAccepted() {
        assertNotNull(Parser.parseDataLine("E | 0 | ping | 2026-01-05T09:00 | 2026-01-05T09:00"));
    }

    @Test
    public void hasForceMarker_trailingMarker_returnsTrue() {
        assertTrue(Parser.hasForceMarker("meeting /from 2026-01-05 /to 2026-01-06 /force"));
        assertTrue(Parser.hasForceMarker("meeting /force   "));
    }

    @Test
    public void hasForceMarker_markerNotAtEnd_returnsFalse() {
        assertFalse(Parser.hasForceMarker("/force meeting"));
        assertFalse(Parser.hasForceMarker("meeting /from 2026-01-05 /to 2026-01-06"));
    }

    @Test
    public void removeForceMarker_trailingMarker_isStripped() {
        assertEquals("meeting /from 2026-01-05 /to 2026-01-06",
                Parser.removeForceMarker("meeting /from 2026-01-05 /to 2026-01-06 /force"));
    }

    @Test
    public void removeForceMarker_noMarker_returnsTrimmedInput() {
        assertEquals("meeting", Parser.removeForceMarker("  meeting  "));
    }

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
