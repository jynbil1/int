package hanpoom.internal_cron.crons.dashboard.slack.mapper;

import hanpoom.internal_cron.crons.dashboard.slack.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProductMapper {

    List<ProductVO> getUsingProductList();
    // 어드민에서 비공개 상품으로 업데이트
    void adminPrivateUpdate(List<String> products);

}
