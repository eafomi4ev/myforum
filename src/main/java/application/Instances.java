package application;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Instances {

    @NotNull
    public static Timestamp getTimestampFromString(String time) {
        return new Timestamp(ZonedDateTime.parse(ZonedDateTime.parse(time).format(DateTimeFormatter.ISO_INSTANT)).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
