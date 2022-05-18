package hanpoom.internal_cron.crons.dashboard.order.mapper;

import hanpoom.internal_cron.crons.dashboard.order.vo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {

    List<Order> getIncompleteParentOrders();
    void updateCompletOrders(List<Integer> orderNos);
}
