package nova;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Turns text into values the rest of the program can use.
 * It handles both directions of input: what the user types at the prompt, and
 * what was previously written to the data file. Every method returns null
 * instead of throwing when the text cannot be understood, so callers can
 * report the problem and carry on rather than unwinding a stack.
 */
public class Parser {
    /**
     * Input format accepted when the user supplies a date and a time.
     * STRICT is needed because ofPattern() otherwise resolves leniently and
     * would quietly move an impossible date like 2019-02-30 to Feb 28.
     * STRICT in turn requires "uuuu" rather than "yyyy": y is the year-of-era,
     * which strict resolving refuses to interpret without an era field, while
     * u is the plain year and stands on its own.
     */
    private static final DateTimeFormatter INPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Describes the formats parseDateTime accepts, for showing to the user.
     * It lives beside the formatters so that adding a format and updating
     * this sentence are the same edit.
     */
    static final String DATE_FORMAT_HINT =
            "Dates must look like 2019-10-15 or 2019-10-15 1800.";

    /**
     * Rebuilds a single task from one line of the data file.
     * The line is checked before every field is read, so a truncated or
     * corrupted entry is rejected rather than throwing.
     *
     * @param dataLine one saved line, e.g. "D | 0 | return book | 2019-10-15T18:00".
     * @return the reconstructed task, or null if the line is malformed.
     */
    static Task parseDataLine(String dataLine) {
        String[] dataLineComponents = dataLine.split("\\|");

        // Every task type needs at least a type, a done flag and a description.
        if (dataLineComponents.length < 3) {
            return null;
        }

        String markedField = dataLineComponents[1].trim();
        if (!isInteger(markedField)) {
            return null;
        }

        String typeOfTask = dataLineComponents[0].trim();
        boolean isMarked = Integer.parseInt(markedField) == 1;
        String taskStored = dataLineComponents[2].trim();

        if (taskStored.isEmpty()) {
            return null;
        }

        switch (typeOfTask) {
            case "T":
                return new ToDo(taskStored, isMarked);
            case "D":
                if (dataLineComponents.length < 4) {
                    return null;
                }
                LocalDateTime by = parseStoredDateTime(dataLineComponents[3]);
                if (by == null) {
                    return null;
                }
                return new Deadline(taskStored, isMarked, by);
            case "E":
                if (dataLineComponents.length < 5) {
                    return null;
                }
                LocalDateTime from = parseStoredDateTime(dataLineComponents[3]);
                LocalDateTime to = parseStoredDateTime(dataLineComponents[4]);
                if (from == null || to == null) {
                    return null;
                }
                return new Event(taskStored, isMarked, from, to);
            default:
                return null;
        }
    }

    /**
     * Reads back a date-time written by toDataString(), which stores values in
     * ISO-8601. Lines saved before dates were typed hold free text such as
     * "2pm" and are rejected here, so the caller skips and counts them.
     *
     * @param storedField one date field from the data file.
     * @return the parsed value, or null if the field is not valid ISO-8601.
     */
    static LocalDateTime parseStoredDateTime(String storedField) {
        try {
            return LocalDateTime.parse(storedField.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Reports whether some text is a whole number, used to check the task
     * number given to commands like mark and delete before parsing it.
     *
     * @param str the text to test, may be null.
     * @return true if Integer.parseInt would succeed.
     */
    static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Turns a date the user typed into a LocalDateTime.
     * Accepts "yyyy-MM-dd HHmm", or "yyyy-MM-dd" on its own, in which case the
     * time becomes midnight and is later left out of the display.
     *
     * @param rawDate the text between the command markers, already trimmed.
     * @return the parsed value, or null if it matched neither format.
     */
    static LocalDateTime parseDateTime(String rawDate) {
        try {
            return LocalDateTime.parse(rawDate, INPUT_WITH_TIME);
        } catch (DateTimeParseException e) {
            // Not a date-and-time; fall through and try a bare date.
        }

        try {
            return LocalDate.parse(rawDate).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
