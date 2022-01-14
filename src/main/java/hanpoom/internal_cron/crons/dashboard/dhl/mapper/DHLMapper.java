package hanpoom.internal_cron.crons.dashboard.dhl.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.utility.shipment.dhl.vo.DHLShipmentTrackingVO;

@Mapper
@Repository
public interface DHLMapper {
    public ArrayList<DHLShipmentTrackingVO> getTrackableOrders(String start_dtime, String end_dtime);
    public Integer insertDeliveredShipment(String param);
    public Integer insertErrorShipment(String param);

}
