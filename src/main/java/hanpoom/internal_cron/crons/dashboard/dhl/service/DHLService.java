package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.mapper.DHLMapper;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;
import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponse;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponseStorage;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentEvent;

@Service
public class DHLService {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DHLMapper mapper;
    private DHLShipmentTrackingService dHLShipmentTrackingService;

    public DHLService(DHLMapper mapper, DHLShipmentTrackingService dHLShipmentTrackingService) {
        this.mapper = mapper;
        this.dHLShipmentTrackingService = dHLShipmentTrackingService;
    }

    // Service
    // Investigate Shipped Orders
    public DHLTrackingResult investigateNProcessShippedOrders() {

        List<DHLTrackingVO> orderTrackingList = getTrackableOrders();
        List<List<String>> trackingSets = new ArrayList<>();
        List<String> trackingNos = new ArrayList<>();

        List<DHLTrackingVO> deliveredOrders = new ArrayList<>();
        List<DHLTrackingVO> customIssueOrders = new ArrayList<>();
        List<DHLTrackingVO> otherIssueOrders = new ArrayList<>();

        int limit = 50;
        int index = 1;
        int accIndex = 1;
        for (DHLTrackingVO orderTracking : orderTrackingList) {
            if (index > limit || orderTrackingList.size() == accIndex) {
                trackingSets.add(trackingNos);
                trackingNos = new ArrayList<>();
                index = 1;
            }
            trackingNos.add(orderTracking.getTracking_no());
            ++index;
            ++accIndex;
        }

        for (List<String> trackingSet : trackingSets) {

            DHLTrackingResponseStorage storage = dHLShipmentTrackingService.trackShipments(trackingSet);

            // 반환된 매 운송장 값의 결과.
            for (DHLTrackingResponse response : storage.getResponses()) {

                // 정상적인 조회가 아닌경우,
                if (!response.getStatus().getActionStatus().equals("Success")) {
                    // 해당 값에 대한 오류 표기 하고
                    continue;
                }
                // 마지막 이벤트 데이터
                response.getShipmentEvents().get(response.getShipmentEvents().size() - 1);

            }
        }
        // orderTrackingList
        // try {
        // } catch (Exception e) {

        // }
        return null;
    }

    // DB DATA Processing
    private ArrayList<DHLTrackingVO> getTrackableOrders() {
        try {
            // 지금 현재부터 60일 전 까지의 Shipped 데이터를 가져오기.
            String start_dtime = LocalDateTime.now().minusDays(60)
                    .format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            String end_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));

            Map<String, String> dateRange = new HashMap<>();
            dateRange.put("start_dtime", start_dtime);
            dateRange.put("end_dtime", end_dtime);

            return mapper.getTrackableOrders(dateRange);

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

    // Filter the only deliverd Orders
    public void getDeliveredOrders() {
    }

    // Filter the delayed days for the undelivered orders
    // 10 days of working days (2 weeks)
    public void getDelayedDays() {
    }

    // Check the orders tracking no that are not solved.
    public void checkUnresolvedOrders() {
    }

    // public
}
