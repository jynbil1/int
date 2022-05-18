package hanpoom.internal_cron.crons.dashboard.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    private int orderNo;
    private String orderStatus;
    private String orderDate;

    private List<Order> childOrders;

    private int parentOrderNo;
    private String parentOrderStatus;
    private String parentOrderDate;

    @Override
    public boolean equals(Object obj) {
        Order order = (Order) obj;
        if (this.parentOrderNo == order.getOrderNo()) {
            return true;
        } else {
            return false;
        }
    }
}
