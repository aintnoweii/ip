package nova;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    /** Input format accepted when the user supplies a date and a time. */
    private static final DateTimeFormatter INPUT_WITH_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Describes the formats parseDateTime accepts, for showing to the user.
     * It lives beside the formatters so that adding a format and updating
     * this sentence are the same edit.
     */
    static final String DATE_FORMAT_HINT =
            "Dates must look like 2019-10-15 or 2019-10-15 1800.";

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

    static LocalDateTime parseStoredDateTime(String storedField) {
        try {
            return LocalDateTime.parse(storedField.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

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
