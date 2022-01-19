package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.dhl.mapper.DHLMapper;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;
import hanpoom.internal_cron.utility.shipment.dhl.config.DHLShipmentStatusCode;
import hanpoom.internal_cron.utility.shipment.dhl.service.DHLShipmentTrackingService;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponse;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponseStorage;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentEvent;
import lombok.Getter;

@Service
@Getter
public class DHLService {

    private List<DHLTrackingVO> deliveredOrders;
    private List<DHLTrackingVO> customsIssueOrders;
    private List<DHLTrackingVO> otherIssueOrders;
    private List<DHLTrackingVO> delayedOrders;
    private List<DHLTrackingVO> untrackableOrders;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DHLMapper mapper;
    private DHLShipmentTrackingService dHLShipmentTrackingService;
    private DHLShipmentStatusCode shipmentStatusCode;

    public DHLService(DHLMapper mapper, DHLShipmentTrackingService dHLShipmentTrackingService,
            DHLShipmentStatusCode shipmentStatusCode) {
        this.mapper = mapper;
        this.dHLShipmentTrackingService = dHLShipmentTrackingService;
        this.shipmentStatusCode = shipmentStatusCode;
    }

    // Service
    // Investigate Shipped Orders
    public DHLTrackingResult investigateNProcessShippedOrders() {

        List<DHLTrackingVO> orderTrackingList = getTrackableOrders();
        List<List<String>> trackingSets = new ArrayList<>();
        List<String> trackingNos = new ArrayList<>();

        int limit = 50;
        int index = 1;
        int accIndex = 1;
        // 한번에 많은 데이터를 요청하면 안되므로, 50개씩 묶는작업이다.
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
        // 50개씩 묶은 데이터를 매 요청에 담아 조회하고 데이터를 구분한다.
        // DHL 배송 상태를 파악하는 JSON 파일 객체를 담고 있다
        JSONObject shipmentCode = new JSONObject();
        try {
            shipmentCode = shipmentStatusCode.getShipmentStatusJSON();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        List<DHLTrackingVO> deliveredOrders = new ArrayList<>();
        List<DHLTrackingVO> customIssueOrders = new ArrayList<>();
        List<DHLTrackingVO> otherIssueOrders = new ArrayList<>();
        List<DHLTrackingVO> untrackableOrders = new ArrayList<>();
        List<DHLTrackingVO> delayedOrders = new ArrayList<>();

        for (List<String> trackingSet : trackingSets) {
            DHLTrackingResponseStorage storage = dHLShipmentTrackingService.trackShipments(trackingSet);

            // 반환된 매 운송장 값의 결과.
            for (DHLTrackingResponse response : storage.getResponses()) {

                // 정상적인 조회가 아닌경우,
                if (!response.getStatus().getActionStatus().equals("Success")) {
                    // 해당 값에 대한 오류 표기 하고
                    untrackableOrders.add(new DHLTrackingVO());
                    continue;
                }
                // 데이터 중에서 배송 완료가 된 이벤트 코드가 있는지 확인이 필요.
                // DHL 전산의 이유로 인해 이상한 데이터 처리(순서가 이상함 등 )가 있을 수 있음.

                JSONObject priortyLevel = shipmentCode.getJSONObject("priortyLevel");
                JSONObject shipmentEventCode = shipmentCode.getJSONObject("status");

                int currentPriorityNo = 99;
                String currentEventCode = "";

                ShipmentEvent prioritizedEvent = new ShipmentEvent();
                for (ShipmentEvent event : response.getShipmentEvents()) {
                    currentEventCode = shipmentEventCode.getJSONObject(event.getEventCode()).getString("hpGroup");
                    if (priortyLevel.getInt(currentEventCode) < currentPriorityNo) {
                        prioritizedEvent = event;
                        currentPriorityNo = priortyLevel.getInt(currentEventCode);
                    }
                }

                //

                // Highest Priority Processable Data
                // prioritizedEvent

                // 상황에 따라 담아 처리할 리스트 마련.

                // 한품 내에서 지정한 기준에 의거하여 배송 상태에 따른 각 다른 업무를 처리한다.
                String eventCase = shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode())
                        .getString("hpGroup");
                switch (eventCase) {
                    case "delivered":
                        deliveredOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    case "urgency-customs":
                        customIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime(),
                                prioritizedEvent.getFurtherDetails(),
                                prioritizedEvent.getNextSteps()));

                        break;
                    case "urgency-shipment":
                        otherIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    case "refuse":
                        otherIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    case "exception":
                        otherIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    case "clearance-delay":
                        otherIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    // 이하 차후 목적을 위해 남겨놓음.
                    // case "clearance-process":
                    // break;
                    // case "ok":
                    // break;
                    // case "in-transit":
                    // break;
                    // case "shipment-start":
                    // break;

                    // shipped 인 상태에서 아직도 발송을 안한 케이스의 경우 문제로 빠짐.
                    case "shipment-ready":
                        otherIssueOrders.add(new DHLTrackingVO(
                                response.getShipmentDetail().getShipmentReference(),
                                response.getTrackingNo(),
                                prioritizedEvent.getEventCode(),
                                shipmentEventCode.getJSONObject(prioritizedEvent.getEventCode()).getString("korDesc"),
                                prioritizedEvent.getDate(),
                                prioritizedEvent.getTime()));
                        break;
                    // 위의 조건에도 부합하지 않은 건들은 배송 기간을 조회하여 지연여부를 확인함.
                    default:
                        if (isDelayedShipment(response.getShipmentEvents())) {
                            delayedOrders.add(new DHLTrackingVO(
                                    response.getShipmentDetail().getShipmentReference(),
                                    response.getTrackingNo()));
                        }
                        break;
                }
                this.deliveredOrders = deliveredOrders;
                this.customsIssueOrders = customIssueOrders;
                this.otherIssueOrders = otherIssueOrders;
                this.delayedOrders = delayedOrders;
                this.untrackableOrders = untrackableOrders;
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

    // public Integer insertDeliveredShipment(ArrayList<DHLTrackingVO>
    // deliveredShipments) {
    // String insertManyParams = "";
    // try {
    // for (DHLTrackingVO shipment : deliveredShipments) {
    // insertManyParams += String.format("(%s, '%s', '%s', '%s', '%s', '%s')\n",
    // shipment.getOrder_no(),
    // shipment.getTracking_no(), shipment.getEvent(), shipment.getEvent_code(),
    // shipment.getShipped_dtime(), shipment.getEvent_dtime());
    // if (deliveredShipments.indexOf(shipment) != 0) {
    // insertManyParams += ",";
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return 0;

    // }

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
    public boolean isDelayedShipment(List<ShipmentEvent> shipmentEvents) {
        // Weekend Not Included.
        int delayAllowableDays = 10;
        ShipmentEvent event = new ShipmentEvent();
        try {
            // 마지막 이벤트
            event = shipmentEvents.get(shipmentEvents.size() - 1);
            LocalDate eventDtime = LocalDate
                    .parse(event.getDate(),
                            DateTimeFormatter.ofPattern(DATE_PATTERN));

            LocalDate now = LocalDate.now();

            long days = eventDtime.datesUntil(now)
                    .filter(date -> isWorkingDay(date))
                    .count();

            System.out.println(days);
            if (days >= delayAllowableDays) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY);
    }

    // Check the orders tracking no that are not solved.
    public void checkUnresolvedOrders() {
    }
}
