package hanpoom.internal_cron.crons.dashboard.slack.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DashboardMapper {
    // 신규 구매 + 신규 가입. 전에 신규 가압을 했어도 첫 구매를 한 사람이 중복 집계됨.
    public String getDailyNewCustomers();

    // 순수하게 신규 가입한 사람의 일일 데이터를 가져옴.
    public String getDailyTrueNewCustomers();

    // 순수하게 신규 가입한 사람의 월 데이터를 가져옴.
    public String getMonthlyNewCustomers();

    // 지금까지 전체 회원수를 가져옴.
    public String getTotalCustomers();

    // 해당 달 내 전체 회원수.
    public String getNewUsers(@Param("start_dtime") String start_dtime,
            @Param("end_dtime") String end_dtime);
}
