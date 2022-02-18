package hanpoom.internal_cron.crons.dashboard.dhl.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLService;
import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLShipmentHanldingService;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;

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

    @GetMapping("/investigate-shipped-orders-inactive")
    public String testStatus() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 작업이 시작되었습니다.");

        DHLTrackingResult result = dHLService.filterShipments();

        // // 통관 문제
        // System.out.println(dHLService.getCustomsIssueOrders().toString());
        // // 기타 문제
        // System.out.println(dHLService.getOtherIssueOrders().toString());
        // // 배송 완료
        // System.out.println(dHLService.getDeliveredOrders().toString());
        // System.out.println(dHLService.getUntrackableOrders().toString());
        // System.out.println(dHLService.getDelayedOrders().toString());

        if (dHLService.getDeliveredOrders().size() > 0) {
            dHLShipmentHanldingService.processDeliveredOrders(dHLService.getDeliveredOrders());
        }

        if (dHLService.getCustomsIssueOrders().size() > 0) {
            dHLShipmentHanldingService.processCustomsIssueOrders(dHLService.getCustomsIssueOrders());
        }

        if (dHLService.getOtherIssueOrders().size() > 0) {
            dHLShipmentHanldingService.processOtherIssueOrders(dHLService.getOtherIssueOrders());

        }
        if (dHLService.getDelayedOrders().size() > 0) {
            dHLShipmentHanldingService.processDelayedOrders(dHLService.getDelayedOrders());

        }
        if (dHLService.getUntrackableOrders().size() > 0) {
            dHLShipmentHanldingService.processUntrackableOrders(dHLService.getUntrackableOrders());

        }
        if (dHLService.getReturnedOrders().size() > 0) {
            dHLShipmentHanldingService.processReturnedOrders(dHLService.getReturnedOrders());

        }


        String executeMessage = now.format(DateTimeFormatter.ofPattern(DATE_PATTERN + " HH"));
        String messageText = "%s시 발송 모니터링 현황\n"
                + "---------------------------------------------------\n"
                + "배송 완료: %s 건\n" + "배송 지연: %s 건\n"
                + "통관 문제: %s 건\n\n" + "조회 불가: %s 건\n"
                + "이외 문제: %s 건\n" + "반송 완료: %s 건\n"
                + "---------------------------------------------------\n"
                + "배송중: %s 건\n" 
                + "<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=448567097|문제 보러가기>";
        System.out.println(String.format(messageText,
                        executeMessage,
                        result.getTotalDeliveries(),
                        result.getTotalDelays(),
                        result.getTotalCustomsIssues(),
                        result.getTotalUntrackables(),
                        result.getTotalOtherIssues(),
                        result.getTotalReturned(),
                        result.getTotalInTransit()));
        // boolean isSent = dHLShipmentHanldingService.sendSlackMessage(
        //         String.format(messageText,
        //                 executeMessage,
        //                 result.getTotalDeliveries(),
        //                 result.getTotalDelays(),
        //                 result.getTotalCustomsIssues(),
        //                 result.getTotalUntrackables(),
        //                 result.getTotalOtherIssues(),
        //                 result.getTotalReturned(),
        //                 result.getTotalInTransit()));
        // if (!isSent) {
        //     System.out.println("현황 결과를 출력하지 못했습니다.");
        // } else {
        //     System.out.println("현황 데이터를 성공적으로 출력했습니다.");
        // }

        LocalDateTime then = LocalDateTime.now();
        String endTime = then.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        long timeSpent = LocalDateTime.from(now).until(then, ChronoUnit.SECONDS);
        System.out.println(endTime + " 에 작업이 끝마쳤습니다.");
        System.out.println(String.format("소요시간: %s 분 %s 초",
                String.valueOf(timeSpent / 60),
                String.valueOf(timeSpent % 60)));

        return "test";
    }


    // 기존에 문제가 되어 스프레드 시트에 올라온 건들 중, 완료되지 않은 건들을 파악한다.
    // 파악한 건들 중 시트에 적혀있는 운송장 번호와 현재 wphpm_postmeta 의 값이 다른지 확인한다.
    // 다르면 업데이트 쳐준다.
    // @GetMapping("/recheck-tracking-no-validity")
    // public void recheckTrackingNoValidity() {
    //     LocalDateTime now = LocalDateTime.now();
    //     String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
    //     System.out.println(executeTime + " 에 운송장 갱신여부 확인 작업이 시작되었습니다.");
        
    //     dHLShipmentHanldingService.updateSheetRowToNewTrackingNo();
        
    // }

    // 이미 처리했던 데이터들을 조회하여 문제가 발생한 건들을 다시 파악한다.
    @GetMapping("/update-issue-order")
    public void updateShipmentStatusforIssueOrders() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 문제건 재검사 파악 작업이 시작되었습니다.");

        // 1. 엑셀 데이터 가져오기
        // 2. 운송장 번호로 현황 조회하기
        // 3. 완료 되었으면, 완료 일자, 완료일자 - 발송일자 => 소요기간
        // 4. 3번 값 갱신하고 완료여부에 체크하기.
        dHLShipmentHanldingService.checkNUpdateCompleteShipments();

    }
   

    // @GetMapping("/getSingleShipment")
    // public void getSingleShipment(){
    //     DHLTrackingVO trackingVo = dHLService.filterShipment(new DHLTrackingVO("638908","6489710614"));
    //     System.out.println(trackingVo);
    // }
}
