package hanpoom.internal_cron.crons.dashboard.cronjobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hanpoom.internal_cron.crons.dashboard.dhl.service.DHLReport;

@Component
public class DHLCron {

    @Autowired
    private DHLReport dhlReport;
    private static final String TZ_KOREA = "Asia/Seoul";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    // Runs when the Delivery Operations are ended up on the 6 PM in the evening in
    // US CA. (GMT-8)
    // 미국 CA (GMT-8) 오후 6 시 이후, 대부분의 배달 작업이 끝나는 시간대를 고려하여 스케줄러를 수행
    // @Scheduled(cron = "0 0 11 * * TUE-SAT", zone = TZ_KOREA)
    @Scheduled(cron = "0 0 11 * * *", zone = TZ_KOREA)
    public void dhlShipmentMonitorCron() {
        dhlReport.monitorShipments();
    }

    // 기존에 문제가 되어 스프레드 시트에 올라온 건들 중, 완료되지 않은 건들을 파악한다.
    // 파악한 건들 중 시트에 적혀있는 운송장 번호와 현재 wphpm_postmeta 의 값이 다른지 확인한다.
    // 다르면 업데이트 쳐준다.
    @Scheduled(cron = "0 30 11 * * *", zone = TZ_KOREA)
    public void upateNewShipmentCron() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 운송장 갱신여부 확인 작업이 시작되었습니다.");

        // 1. 엑셀 데이터 가져오기
        // 2. order no 로 wphpm_postmeta 데이터 찾기
        // 3. DB 데이터랑 엑셀 데이터 비교
        // 4. 다른거 갱신
        dhlReport.updateSheetRowToNewTrackingNo();

    }

    // 이미 처리했던 데이터들을 조회하여 문제가 발생한 건들을 다시 파악한다.
    @Scheduled(cron = "0 0 12 * * *", zone = TZ_KOREA)
    public void recheckIssueShipmentCron() {
        LocalDateTime now = LocalDateTime.now();
        String executeTime = now.format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
        System.out.println(executeTime + " 에 문제건 재검사 파악 작업이 시작되었습니다.");

        // 1. 엑셀 데이터 가져오기
        // 2. 운송장 번호로 현황 조회하기
        // 3. 완료 되었으면, 완료 일자, 완료일자 - 발송일자 => 소요기간
        // 4. 3번 값 갱신하고 완료여부에 체크하기.
        dhlReport.checkNUpdateCompleteShipments();

    }

}
