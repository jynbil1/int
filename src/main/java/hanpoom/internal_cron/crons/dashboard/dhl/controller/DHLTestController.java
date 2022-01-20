package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLService;
import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLShipmentHanldingService;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;

@RestController
public class DHLTestController {

    private DHLService dHLService;
    private DHLShipmentHanldingService dHLShipmentHanldingService;

    public DHLTestController(DHLService dHLService,
            DHLShipmentHanldingService dHLShipmentHanldingService) {
        this.dHLService = dHLService;
        this.dHLShipmentHanldingService = dHLShipmentHanldingService;

    }

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("/investigate-issue-occurred-orders")
    public String investigateIssueOccurredOrders() {
        return null;
    }

    @GetMapping("/investigate-shipped-orders")
    public String testStatus() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 작업이 시작되었습니다.");
        String executeMessage = now.format(DateTimeFormatter.ofPattern(DATE_PATTERN + " HH"));
        DHLTrackingResult result = dHLService.investigateNProcessShippedOrders();

        // 통관 문제
        System.out.println(dHLService.getCustomsIssueOrders().toString());
        // 기타 문제
        System.out.println(dHLService.getOtherIssueOrders().toString());
        // 배송 완료
        System.out.println(dHLService.getDeliveredOrders().toString());
        System.out.println(dHLService.getUntrackableOrders().toString());
        System.out.println(dHLService.getDelayedOrders().toString());

        String messageText = "%s시 발송 모니터링 현황\n"
                + "배송 완료: %s 건\n" + "배송 지연: %s 건\n"
                + "통관 문제: %s 건\n" + "이외 문제: %s 건\n";

        boolean isSent = dHLShipmentHanldingService.sendSlackMessage(
                String.format(messageText,
                        result.getTotalDeliveries(),
                        result.getTotalDelays(),
                        result.getTotalCustomsIssues(),
                        result.getTotalOtherIssues()));
        if (!isSent) {
            System.out.println("현황 결과를 출력하지 못했습니다.");
        } else {
            System.out.println("현황 데이터를 성공적으로 출력했습니다.");
        }

        LocalDateTime then = LocalDateTime.now();
        String endTime = then.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        long timeSpent = LocalDateTime.from(now).until(then, ChronoUnit.SECONDS);
        System.out.println(endTime + " 에 작업이 끝마쳤습니다.");
        System.out.println(String.format("소요시간: %s 분 %s 초",
                String.valueOf(timeSpent / 60),
                String.valueOf(timeSpent % 60)));
                
        return "test";
    }
}
