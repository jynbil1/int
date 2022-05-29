package hanpoom.internal_cron.crons.dashboard.order.service;

import hanpoom.internal_cron.crons.dashboard.order.enumerate.OrderStatus;
import hanpoom.internal_cron.crons.dashboard.order.mapper.OrderMapper;
import hanpoom.internal_cron.crons.dashboard.order.vo.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderMapper orderMapper;

    public Integer updateParentOrders() {
        // 1. 부모의 값은 processing 인데, 자식의 값은 completed 혹은 child...-refunded 인 것들만 가져온다.
        List<Order> preRefinedOrders = getIncompleteParentOrders();
        if (preRefinedOrders.isEmpty()) {
            return 0;
        }

        List<Order> orders = mapByOrderNo(preRefinedOrders);
        List<Order> filteredOrders = filterCompleteOrders(orders);
        // 2. 가져온 값들 중 자식의 값 모두가 completed 나 refunded 면 해당 parent 값의 상태를 바꾼다.
        List<Integer> orderNos = filteredOrders.stream().map(Order::getOrderNo).collect(Collectors.toList());
        try {
            if (orderNos.isEmpty() || orderNos.size() <= 1) {
                return 0;
            }
            orderMapper.updateCompleteOrders(orderNos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return orderNos.size();
    }

    private List<Order> filterCompleteOrders(List<Order> orders) {
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : orders) {
            int count = 0;
            for (Order childOrder : order.getChildOrders()) {
                // 자식 주문서의 모든 상태 값이 모두 조건에 맞을 경우 필터 대상이 되는 리스트에 담는다.
                if (OrderStatus.CHILD_STATUS.getStatusList().contains(childOrder.getOrderStatus())) {
                    ++count;
                    if (isMatched(order.getChildOrders(), count)) {
                        filteredOrders.add(order);
                    }
                }
            }
        }
        return filteredOrders;
    }

    private boolean isMatched(List<?> list, int count) {
        if (list.size() == count) {
            return true;
        } else {
            return false;
        }
    }
//    private List<Order> mapByOrderNo(List<Order> orders){
//        List<Order> newOrders = new ArrayList<>();
//        List<Order> childOrders = new ArrayList<>();
////        boolean isFirst = true;
//        Order previousOrder = new Order();
//        int seq = 0;
//        for (Order order: orders){
//            ++ seq;
//            if (seq <= 1) {
////                isFirst = false;
//                childOrders.add(order);
//                previousOrder = order;
//                continue;
//            } else if (order.equals(previousOrder)) {
//                childOrders.add(
//                        Order.builder()
//                                .orderNo(order.getOrderNo())
//                                .orderStatus(order.getOrderStatus())
//                                .orderDate(order.getOrderDate())
//                                .build()
//                );
//            } else {
//                newOrders.add(Order.builder()
//                                .orderNo(order.getParentOrderNo())
//                                .orderStatus(order.getParentOrderStatus())
//                                .orderDate(order.getParentOrderDate())
//                                .childOrders(childOrders)
//                        .build());
//                childOrders = new ArrayList<>();
//                childOrders.add(order);
//            }
//            previousOrder = order;
//            if (seq == orders.size()) {
//                newOrders.add(Order.builder()
//                        .orderNo(order.getParentOrderNo())
//                        .orderStatus(order.getParentOrderStatus())
//                        .orderDate(order.getParentOrderDate())
//                        .childOrders(childOrders)
//                        .build());
//            }
//        }
//
//        return newOrders;
//    }

    private List<Order> mapByOrderNo(List<Order> orders) {
        List<Order> newOrders = new ArrayList<>();
        for (Order order : orders) {
            if (newOrders.contains(order)) {
                Order newOrder = newOrders.stream().filter(ord -> ord.getOrderNo() == order.getParentOrderNo()).findFirst().get();
                newOrder.getChildOrders().add(
                        Order.builder()
                                .orderNo(order.getOrderNo())
                                .orderStatus(order.getOrderStatus())
                                .orderDate(order.getOrderDate())
                                .build());
            } else {
                List<Order> childOrders = new ArrayList<>();
                childOrders.add(Order.builder()
                        .orderNo(order.getOrderNo())
                        .orderStatus(order.getOrderStatus())
                        .orderDate(order.getOrderDate())
                        .build());

                newOrders.add(
                        Order.builder()
                                .orderNo(order.getParentOrderNo())
                                .orderStatus(order.getParentOrderDate())
                                .orderDate(order.getParentOrderDate())
                                .childOrders(childOrders)
                                .build()
                );
            }
        }
        return newOrders;
    }

    private List<Order> getIncompleteParentOrders() {
        try {
            return orderMapper.getIncompleteParentOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
