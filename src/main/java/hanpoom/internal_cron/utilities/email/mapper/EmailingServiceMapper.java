package hanpoom.internal_cron.utilities.email.mapper;

import hanpoom.internal_cron.utilities.email.vo.EmailCredentialsVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface EmailingServiceMapper {
    EmailCredentialsVO getEmailCredentialsVO();
}
