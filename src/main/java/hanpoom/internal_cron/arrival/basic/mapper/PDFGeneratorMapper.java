package hanpoom.internal_cron.arrival.basic.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.arrival.basic.vo.ArrivalProductVO;

@Mapper
@Repository
public interface PDFGeneratorMapper {
    ArrivalProductVO getInProductLabelDatum(String arrival_seq);
    ArrayList<ArrivalProductVO> getInProductLabelData(ArrayList<String> arrival_seqs);
}
