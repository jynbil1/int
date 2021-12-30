package hanpoom.internal_cron.arrival.basic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import hanpoom.internal_cron.arrival.basic.vo.ArrivalCriteria;
import hanpoom.internal_cron.arrival.basic.vo.ArrivalProductVO;
import hanpoom.internal_cron.arrival.basic.vo.OrderProductVO;

@Repository
@Mapper
public interface ArrivalProductMapper {

	Integer insertArrivalProduct(ArrivalProductVO product);
	Integer insertStockingArrivalProduct(ArrivalProductVO product);
	Integer insertOperationArrivalProduct(ArrivalProductVO product);
	Integer deleteArrivalProduct(ArrivalProductVO product);
	ArrayList<ArrivalProductVO> getArrivalProductToday(); 
	ArrayList<ArrivalProductVO> getArrivalProductHistory(ArrivalCriteria criteria); 
	Integer getTotalItems(ArrivalCriteria pageCriteria);
	ArrayList<OrderProductVO> getOrderProduct();
	
	ArrayList<ArrivalProductVO> getArrivedProductListByDate(String arrived_start_date, String arrived_end_date);

}
