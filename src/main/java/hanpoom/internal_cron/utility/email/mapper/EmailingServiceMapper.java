package hanpoom.internal_cron.utility.email.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.utility.email.vo.EmailCredentialsVO;

@Repository
@Mapper
public interface EmailingServiceMapper {
    EmailCredentialsVO getEmailCredentialsVO();
}
