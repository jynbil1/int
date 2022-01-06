package hanpoom.internal_cron.utility.calendar.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import com.google.api.client.util.DateTime;

import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_COMPARE_PATTERN = "yyyyMMdd";

    public String getStartofYesterday() {
        String resultDatetime = null;
        try {
            // 어제 일자 찾기
            LocalDate now = LocalDate.now();
            now = now.minusDays(1);

            resultDatetime = now.atTime(0, 0, 0).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;
    }

    public String getEndofYesterday() {
        String resultDatetime = null;
        try {
            // 어제 일자 찾기
            LocalDate now = LocalDate.now();
            now = now.minusDays(1);

            resultDatetime = now.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;
    }

    public String getPreviousWeekMonday() {
        // 해당 해의 첫주가 월요일 기준 7일이 안되면, plusweek(nthweek -1) 을 할 것.
        // 그러면 해당 주는 작년의 연말일들과 함께 계산이 될것이다.
        // started from sun to sat
        String resultDatetime = null;
        try {
            LocalDate now = LocalDate.now();
            LocalDate firstModayOfMonth = now.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
            resultDatetime = firstModayOfMonth.minusDays(7)
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;
    }

    public String getPreviousWeekSunday() {
        // started from sun to sat
        String resultDatetime = null;
        try {
            LocalDate now = LocalDate.now();
            LocalDate firstModayOfMonth = now.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
            resultDatetime = firstModayOfMonth.minusDays(1)
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN + " 23:59:59"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;
    }

    public String getStartOfYear(int year) {
        String resultDatetime = null;
        try {
            resultDatetime = LocalDateTime.of(year, 1, 1, 0, 0, 0).format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
            return resultDatetime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;

    }

    public String getEndOfYearOpt(int year) {
        // Gets the last datetime of the input year.
        // If the input year is more future than the current time, it retreives now
        String resultDatetime = null;
        try {
            LocalDateTime lastDayofYear = LocalDateTime.of(year, 12,31,23,59,59);
            LocalDateTime now = LocalDateTime.now();

            if (lastDayofYear.isBefore(now)){
                return lastDayofYear.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
            } else {
                return now.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDatetime;
    }
}
