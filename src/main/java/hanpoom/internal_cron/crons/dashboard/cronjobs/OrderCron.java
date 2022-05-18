package hanpoom.internal_cron.crons.dashboard.cronjobs;

import hanpoom.internal_cron.crons.dashboard.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderCron {
    private OrderService orderService;

    // 배송 모니터링이 끝나면 수행한다.
    @Scheduled(cron = "* 30 12 * * *", zone = "Asia/Seoul")
    public void updateParentOrders(){
        System.out.println("자식 주문 값을 확인하여 부모 값을 갱신합니다.");
        Integer updatedOrders = orderService.updateParentOrders();
        System.out.println(updatedOrders + " 건이 처리 완료 되었습니다.");

    }
}
