package hanpoom.internal_cron.crons.dashboard.fedex.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;

@Repository
@Mapper
public interface FedexMapper {
    
    public List<OrderShipment> getOrderShipments();
    public List<OrderShipment> getShipments(List<Integer> orderNos);
    public Integer insertDeliveredShipments(List<OrderShipment> orders);
    public Integer insertErrorShipments(List<OrderShipment> orders);
    
}
