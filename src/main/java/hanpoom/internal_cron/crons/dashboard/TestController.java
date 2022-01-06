package hanpoom.internal_cron.crons.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import hanpoom.internal_cron.utility.calendar.service.CalendarService;

@Controller
public class TestController {

    private CalendarService calendar;

    public TestController(CalendarService calendar) {
        this.calendar = calendar;
    }

    @GetMapping("/calendar")
    public String getCalendarTest() {
        System.out.println(calendar.getStartOfYear(2021));
        System.out.println(calendar.getEndOfYearOpt(2021));
        System.out.println(calendar.getEndOfYearOpt(2019));

        return "test";
    }
}
