package hanpoom.internal_cron.utility.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class CalendarFormatter {
    public static final String DATETIME = "yyyy-MM-dd HH:mm";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TZONE_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    public static String toKoreanDate(String strDateFormat) {
        StringBuilder date = new StringBuilder();
        String[] dateEl = strDateFormat.split("[$&+,:;=?@#|'<>.^*()%!-]");

        if (dateEl.length > 3) {
            throw new IllegalArgumentException();
        }

        date.append(dateEl[0]);
        date.append("년 ");
        date.append(dateEl[1]);
        date.append("월 ");
        date.append(dateEl[2]);
        date.append("일");
        return date.toString();

    }

    public static String toKoreanDateTime(String strDateTimeFormat) {
        strDateTimeFormat = strDateTimeFormat.replace(" ", "-");
        StringBuilder date = new StringBuilder();
        String[] dateEl = strDateTimeFormat.split("[$&+,:;=?@#|'<>.^*()%!-]");
        
        if (dateEl.length < 4 || dateEl.length > 6) {
            throw new IllegalArgumentException();
        }

        date.append(dateEl[0]);
        date.append("년 ");
        date.append(dateEl[1].replace("0", ""));
        date.append("월 ");
        date.append(dateEl[2].replace("0", ""));
        date.append("일 ");
        date.append(dateEl[3]);
        date.append(":");
        date.append(dateEl[4]);
        date.append(":");
        date.append(dateEl[5]);

        return date.toString();
    }

    public static String toKoreanDate(LocalDate localDate) {
        return toKoreanDate(localDate.format(DateTimeFormatter.ofPattern(DATE)));
    }

    public static String toKoreanDateTime(LocalDateTime localDateTime) {
        return toKoreanDateTime(localDateTime.format(DateTimeFormatter.ofPattern(DATETIME)));
    }
}
