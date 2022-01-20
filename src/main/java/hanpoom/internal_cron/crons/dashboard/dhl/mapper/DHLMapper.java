package hanpoom.internal_cron.crons.dashboard.dhl.mapper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;


@Mapper
@Repository
public interface DHLMapper {
    public ArrayList<DHLTrackingVO> getTrackableOrders(Map<String, String> dateRange);

    public DHLTrackingVO getOrderDetailByTrackingNo(String trackingNo);
    
    public Integer insertDeliveredShipments(String param);
    public Integer insertErrorShipments(String param);

    public ArrayList<DHLTrackingVO> getTrackingNos(String param);

}
