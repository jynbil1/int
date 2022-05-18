package hanpoom.internal_cron.crons.dashboard.order.controller;

import hanpoom.internal_cron.crons.dashboard.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @GetMapping(value = "/order/test")
    public String getOrderTest(){
        System.out.println("자식 주문 값을 확인하여 부모 값을 갱신합니다.");
        Integer updatedOrders = orderService.updateParentOrders();
        System.out.println(updatedOrders + " 건이 처리 완료 되었습니다.");
        return "ok";
    }
}
