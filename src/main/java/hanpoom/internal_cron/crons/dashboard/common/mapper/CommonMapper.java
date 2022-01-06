package hanpoom.internal_cron.crons.dashboard.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CommonMapper {
    public String getRevenue(String start_date, String end_date);
    public String getYearRevenue(int year);
}
