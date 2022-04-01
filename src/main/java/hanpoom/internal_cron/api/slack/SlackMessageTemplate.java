package hanpoom.internal_cron.api.slack;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SlackMessageTemplate {
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    public String successfullyEmailedOrders(int noOfOrders, String shipmentType) {
        return new StringBuilder()
                .append(shipmentType)
                .append("\n 발송 안내: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .append("\n")
                .append(String.valueOf(noOfOrders))
                .append(" 건 발송되었습니다.")
                .toString();
    }

    public String erraneouslyEmailedOrders(Set<Integer> orderNos, String shipmentType) {
        return new StringBuilder()
                .append(shipmentType)
                .append("\n 발송건 메일 발송 실패 안내: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .append("\n")
                .append(String.valueOf(orderNos.size()))
                .append(" 건 발송 안내 메일에 실패하였습니다.\n해당 건은 아래와 같습니다.\n")
                .append(String.valueOf(orderNos.toString()))
                .toString();
    }

    public String erraneosulyUpdatedOrderStatus() {
        return new StringBuilder()
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .append("\n")
                .append("발송 완료 주문서 상태 값 변경 실패 알림.")
                .toString();
    }
}
