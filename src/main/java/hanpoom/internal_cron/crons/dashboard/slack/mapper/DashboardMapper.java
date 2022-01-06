package hanpoom.internal_cron.crons.dashboard.slack.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DashboardMapper {
    public String getNewCustomers();
    public String getTotalCustomers();
}
