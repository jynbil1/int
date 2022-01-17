package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.mapper.DHLMapper;
import hanpoom.internal_cron.utility.shipment.dhl.vo.DHLTrackingVO;

@Service
public class DHLService {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DHLMapper mapper;

    public DHLService(DHLMapper mapper) {
        this.mapper = mapper;
    }

    // DB DATA Processing
    public ArrayList<DHLTrackingVO> getTrackableOrders() {
        try {
            // 지금 현재부터 60일 전 까지의 데이터를 가져오기.
            String start_dtime = LocalDateTime.now().minusDays(60)
                    .format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            String end_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            return mapper.getTrackableOrders(start_dtime, end_dtime);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    public Integer insertDeliveredShipment(ArrayList<DHLTrackingVO> deliveredShipments) {
        String insertManyParams = "";
        try {
            for (DHLTrackingVO shipment : deliveredShipments) {
                insertManyParams += String.format("(%s, '%s', '%s', '%s', '%s', '%s')\n", shipment.getOrder_no(),
                        shipment.getTracking_no(), shipment.getEvent(), shipment.getEvent_code(),
                        shipment.getShipped_dtime(), shipment.getEvent_dtime());
                if (deliveredShipments.indexOf(shipment) != 0) {
                    insertManyParams += ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    public Integer insertErrorShipment(ArrayList<DHLTrackingVO> erraneousShipments) {
        String insertManyParams = "";
        try {
            for (DHLTrackingVO shipment : erraneousShipments) {
                insertManyParams += String.format("(%s, '%s', '%s', '%s', '%s')\n", shipment.getOrder_no(),
                        shipment.getTracking_no(), shipment.getEvent(), shipment.getEvent_code(),
                        shipment.getEvent_dtime());
                if (erraneousShipments.indexOf(shipment) != 0) {
                    insertManyParams += ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // DHL DATA Processing
    // public
}
