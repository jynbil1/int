package hanpoom.internal_cron.crons.dashboard.slack.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import com.itextpdf.text.log.SysoCounter;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.common.mapper.CommonMapper;
import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;
import hanpoom.internal_cron.crons.dashboard.slack.enumerate.CustomerEvaluation;
import hanpoom.internal_cron.crons.dashboard.slack.mapper.DashboardMapper;
import hanpoom.internal_cron.utility.calendar.CalendarManager;
import hanpoom.internal_cron.utility.slack.enumerate.SlackBot;
import hanpoom.internal_cron.utility.slack.service.SlackService;

@Service
public class DashboardService {
    private DashboardMapper dashboardMapper;
    private CommonMapper commonMapper;
    private CalendarManager calendar;

    public DashboardService(DashboardMapper dashboardMapper, CommonMapper commonMapper, CalendarManager calendar) {
        this.dashboardMapper = dashboardMapper;
        this.commonMapper = commonMapper;
        this.calendar = calendar;
    }

    private String getYesterdayRevenue() {
        String yesterdayRevenue;
        try {
            yesterdayRevenue = "$"
                    + commonMapper.getRevenue(
                            new DateRangeVO(calendar.getStartofYesterday(true), calendar.getEndofYesterday(true)));
        } catch (Exception e) {
            yesterdayRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return yesterdayRevenue;
    }

    private String getCurrentYearRevenue() {
        String currentYearRevenue;
        try {
            // get the corresponding startdate and enddate
            int thisYear = LocalDateTime.now().getYear();
            currentYearRevenue = "$"
                    + commonMapper.getRevenue(new DateRangeVO(calendar.getStartOfYear(thisYear, true),
                            calendar.getEndOfYearOpt(thisYear, true)));
        } catch (Exception e) {
            currentYearRevenue = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return currentYearRevenue;
    }

    private String getNewCustomers() {
        String newCustomers;
        try {
            newCustomers = dashboardMapper.getDailyNewCustomers() + " 명";
        } catch (Exception e) {
            newCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return newCustomers;
    }

    private String getTotalCustomers() {
        String totalCustomers;
        try {
            totalCustomers = dashboardMapper.getTotalCustomers() + " 명";
        } catch (Exception e) {
            totalCustomers = "집계에 실패했습니다.";
            e.printStackTrace();
        }
        return totalCustomers;
    }

    private String getNewOrders() {
        String totalOrders = null;
        try {
            totalOrders = commonMapper.getTotalOrders(
                    new DateRangeVO(calendar.getStartofYesterday(true), calendar.getEndofYesterday(true))) + " 건";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalOrders;
    }

    public void reportRevenueDashboard() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 10");

        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Java cron job expression:: " + strDate);

        String message = "***" + strDate + "시 리포팅 ***"
                + "\n전일 매출: " + getYesterdayRevenue()
                // + "\n신규 가입자: " + getNewCustomers()
                + "\n전일 주문건: " + getNewOrders()
                + "\n금년 총 매출: " + getCurrentYearRevenue()
                + "\n누적 회원: " + getTotalCustomers();

        System.out.println(message);
        SlackAPI slack = new SlackAPI();
        try {
            slack.sendMessage(message, SlackBot.REVENUE_BOT.getWebHookUrl());
            System.out.println("슬랙 알림 오케이.");
        } catch (Exception e) {
            System.out.println("결국 실패했습니다.");
        }
        
    }

    public void reportNewUsersDashboard() {
        LocalDateTime nowDT = LocalDateTime.now();
        String now = nowDT.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 10"));
        String startOfTheMonth = nowDT.format(DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00"));
        System.out.println("전체 고객수 파악 처리를 시작합니다.: " + now + " - " + startOfTheMonth);

        String dailyNewCustomers = dashboardMapper.getDailyTrueNewCustomers();
        String monthlyNewCustomers = dashboardMapper.getNewUsers(startOfTheMonth, now);
        String percentile = String.valueOf((Double.valueOf(monthlyNewCustomers.replace(",", ""))
                / CustomerEvaluation.getGoal(nowDT.getMonthValue())) * 100.00 / 1.00);

        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append("`우리는 미국에서 가장 만족스러운 아시안 커머스 마켓이 됩니다!` \n");
        sb.append(String.format("• [*%s 분*]이 하루동안 한품의 회원이 되셨습니다.\n", dailyNewCustomers));
        sb.append(String.format("• %s월 목표 %s / %s명 (%s %%)",
                String.valueOf(nowDT.getMonthValue()),
                monthlyNewCustomers,
                String.valueOf(CustomerEvaluation.getGoal(nowDT.getMonthValue())),
                percentile));

        message = sb.toString();
        SlackAPI slack = new SlackAPI();
        try {
            slack.sendMessage(message, SlackBot.HANPOOM_TEAM.getWebHookUrl());
            System.out.println("슬랙 알림 오케이.");
        } catch (Exception e) {
            System.out.println("결국 실패했습니다.");
        }
    }

    public void reportLastMonthNewUserAchievement() {
        LocalDateTime now = LocalDateTime.now();
        String thisMonth = String.valueOf(now.getMonthValue());
        String endDateTime = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));

        LocalDateTime lastMonth = now.minusMonths(1);
        String startDateTime = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-01 00:00:00"));

        System.out.println(startDateTime + " 부터 " + endDateTime + " 까지에 대한 작업.");

        String lastMonthNewUsers = dashboardMapper.getNewUsers(startDateTime, endDateTime);

        String percentile = String.valueOf(Double.valueOf(lastMonthNewUsers.replace(",", ""))
                / CustomerEvaluation.getGoal(now.getMonthValue()) * 100.00 / 1.00);

        String message = "";
        StringBuilder sb = new StringBuilder();
        sb.append("`우리는 미국에서 가장 만족스러운 아시안 커머스 마켓이 됩니다!` \n");
        sb.append(String.format("• 지난 %s 월 동안 [*%s 분*]이 한품의 회원이 되셨습니다.\n",
            String.valueOf(lastMonth.getMonthValue()), lastMonthNewUsers));

        sb.append(String.format("• %s월 목표 %s / %s명 (%s %%)",
                String.valueOf(lastMonth.getMonthValue()),
                lastMonthNewUsers,
                String.valueOf(CustomerEvaluation.getGoal(now.getMonthValue())),
                percentile));

        message = sb.toString();
        SlackAPI slack = new SlackAPI();
        try {
            slack.sendMessage(message, SlackBot.HANPOOM_TEAM.getWebHookUrl());
            System.out.println("슬랙 알람 오케이.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("슬랙 알람 전송에 실패했음.");
        }
    }
}
