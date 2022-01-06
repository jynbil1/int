package hanpoom.internal_cron.crons.dashboard.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.crons.dashboard.common.vo.DateRangeVO;

@Repository
@Mapper
public interface CommonMapper {
    public String getRevenue(DateRangeVO dateRange);
}
