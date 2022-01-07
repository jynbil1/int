package hanpoom.internal_cron.crons.dashboard.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;

@Repository
@Mapper
public interface CommonMapper {
    public String getRevenue(DateRangeVO dateRange);

    public String getNewUsers(DateRangeVO dateRange);

    public String getNewPurchasers(DateRangeVO dateRange);

    public String getNewRepurchasers(DateRangeVO dateRange);

    public String getTotalOrders(DateRangeVO dateRange);

    public String getTotalMargins(DateRangeVO dateRange);

}
