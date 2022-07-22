package hanpoom.internal_cron.crons.dashboard.slack.mapper;

import hanpoom.internal_cron.crons.dashboard.slack.vo.ExpirationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ExpirationMapper {

    // 전날 유통기한 만료로 인한 손실 재고
    String getYesterdayLoss(@Param("yesterday_date") String yesterday_date);

    // 전날 기준 당일 손실 합계
    String getYesterdayLossSum(@Param("yesterday_date") String yesterday_date);

    // 전날 기준 당월 손실 합계
    String getYesterdayMonthLossSum(@Param("start_dtime") String start_dtime,
                                           @Param("end_dtime") String end_dtime);

    // 운영동 전체 상품 리스트
    List<ExpirationVO> operationExpiration(List<String> products);

    // 운영동 임박 상품 아이디 리스트
    List<String> operationExpirationProduct();

    // 보관동 전체 상품 리스트
    List<ExpirationVO> stockingExpiration(List<String> products);

    //보관동 임박 상품 아이디 리스트
    List<String> stockingExpirationProduct();

    // 유통기한 임박인 상품 업데이트
    void orderStatusUpdate(List<String> products);

    // 어드민에서 비공개 상품으로 업데이트
    void updateToPrivateInAdmin(List<String> products);

    // 운영동 만료 상품 리스트
    List<ExpirationVO> operationExpiredProduct();

    // 보관동 만료 상품 리스트
    List<ExpirationVO> stockingExpiredProduct();
}
