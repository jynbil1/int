package hanpoom.internal_cron.crons.dashboard.dhl.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;


@Mapper
@Repository
public interface DHLMapper {
    public ArrayList<DHLTrackingVO> getTrackableOrders(Map<String, String> dateRange);

    public DHLTrackingVO getOrderDetailByTrackingNo(DHLTrackingVO searchVo);
    
    public Integer insertDeliveredShipments(List<DHLTrackingVO> deliveredShipments);
    public Integer insertErrorShipments(List<DHLTrackingVO> erraneousShipments);

    public ArrayList<DHLTrackingVO> getTrackingNos(List<String> orderNos);

}
