package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private List<DHLTrackingVO> returnedOrders;

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

    public DHLTrackingVO filterShipment(DHLTrackingVO searchVo) {
        // Tracking No 를 조회해서 값이 하나 이상이 나올 수 있기 때문에 대비해야 함.

        DHLTrackingVO trackingVo = getOrderDetailByTrackingNo(searchVo);
        List<String> trackingNos = new ArrayList<>();

        // DHL 배송 상태를 파악하는 JSON 파일 객체를 담고 있다
        JSONObject shipmentCode = new JSONObject();
        try {
            shipmentCode = shipmentStatusCode.getShipmentStatusJSON();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // 데이터 중에서 배송 완료가 된 이벤트 코드가 있는지 확인이 필요.
        // DHL 전산의 이유로 인해 이상한 데이터 처리(순서가 이상함 등 )가 있을 수 있음.
        // status.json 에서 정의된 이벤트 코드 유형의 가중치가 가장 높은 값을 가지고 나온다.
        // JSONObject priortyLevel = shipmentCode.getJSONObject("priorityLevel");
        JSONObject shipmentEventCode = shipmentCode.getJSONObject("status");

        DHLTrackingResponse response = dHLShipmentTrackingService.trackShipment(trackingVo.getTracking_no());

        // 정상적인 조회가 아닌경우,
        try {
            if (!response.getStatus().getActionStatus().equals("Success")) {
                // 해당 값에 대한 오류 표기 하고
                return new DHLTrackingVO(
                        trackingVo.getOrder_no(),
                        trackingVo.getTracking_no(),
                        trackingVo.getShipment_class());
            }
        } catch (NullPointerException np) {
            System.out.println(response.toString());
        }

        // events 들 중 배송 완료 값이 있으면 오케이. 없으면 제일 최신 데이터 가져오기
        int noOfShipmentOnHold = 0;

        // "WC", "FD" 는 타 배송사
        ShipmentEvent selectedEvent = null;

        for (ShipmentEvent event : response.getShipmentEvents()) {
            if (Arrays.asList("BR", "DL", "TP", "DD", "PD", "OK").contains(event.getEventCode())) {
                selectedEvent = event;
                break;
            }
        }

        for (ShipmentEvent currentEvent : response.getShipmentEvents()) {
            if (currentEvent.getEventCode().equals("OH")) {
                noOfShipmentOnHold += 1;
            }
            if (noOfShipmentOnHold >= 3) {
                selectedEvent = currentEvent;
                break;
            }

        }

        if (selectedEvent == null) {
            selectedEvent = response.getShipmentEvents().get(response.getShipmentEvents().size() - 1);
        }

        // 상황에 따라 담아 처리할 리스트 마련.
        // 한품 내에서 지정한 기준에 의거하여 배송 상태에 따른 각 다른 업무를 처리한다.
        // 위에서 가중치가 가장 높은 Event 를 추출함.
        String eventCase = shipmentEventCode.getJSONObject(selectedEvent.getEventCode())
                .getString("hpGroup");

        String orderNo = String.valueOf(response.getShipmentDetail().getShipmentReference());
        DHLTrackingVO responseVo = new DHLTrackingVO();

        responseVo.setOrder_no(orderNo);
        responseVo.setTracking_no(response.getTrackingNo());
        responseVo.setShipped_dtime(response.getShipmentDetail().getShippedDate());
        responseVo.setEvent(shipmentEventCode.getJSONObject(selectedEvent.getEventCode()).getString("korDesc"));
        responseVo.setEvent_code(selectedEvent.getEventCode());
        responseVo.setEvent_dtime(String.format("%s %s", selectedEvent.getDate(), selectedEvent.getTime()));
        responseVo.setShipment_class(trackingVo.getShipment_class());
        responseVo.setShipment_issue_type(eventCase);

        switch (eventCase) {
            case "delivered":
                responseVo.setTypeOfIssue("delivered");

                return responseVo;

            case "returned":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;

            case "urgency-customs":
                responseVo.setEventRemarkDetails(selectedEvent.getFurtherDetails());
                responseVo.setEventRemarkNextSteps(selectedEvent.getNextSteps());
                responseVo.setTypeOfIssue("clearance-issue");
                return responseVo;

            case "urgency-shipment":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;

            case "refuse":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;
            case "exception":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;
            case "clearance-delay":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;
            // 이하 차후 목적을 위해 남겨놓음.
            // case "clearance-process":
            // break;
            // case "closed":
            // break;
            // case "in-transit":
            // break;
            // case "shipment-start":
            // break;

            // shipped 인 상태에서 아직도 발송을 안한 케이스의 경우 문제로 빠짐.
            case "shipment-ready":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;
            // 위의 조건에도 부합하지 않은 건들은 배송 기간을 조회하여 지연여부를 확인함.
            default:
                // 무무면 5일 일반은 10일
                int delayAllowableDays = 10;
                if (!trackingVo.getShipment_class().equals("regular")) {
                    delayAllowableDays = 5;
                }

                if (isDelayedShipment(response.getShipmentEvents(), delayAllowableDays)) {
                    return new DHLTrackingVO(
                            orderNo,
                            response.getTrackingNo(),
                            response.getShipmentDetail().getShippedDate(),
                            trackingVo.getShipment_class());
                }
                break;
        }
        return null;
    }

    public DHLTrackingResult filterShipments() {

        List<DHLTrackingVO> orderTrackingList = getTrackableOrders();
        List<List<String>> trackingSets = new ArrayList<>();
        List<String> trackingNos = new ArrayList<>();

        int limit = 50;
        int index = 1;
        int accIndex = 1;
        // 한번에 많은 데이터를 요청하면 안되므로, 50개씩 묶는작업이다.
        for (DHLTrackingVO orderTracking : orderTrackingList) {

            // 처리해야 할 건이 한번에 처리할 건보다 더 많을 경우,
            if (orderTrackingList.size() > limit) {
                if (index > limit || orderTrackingList.size() == accIndex) {
                    trackingSets.add(trackingNos);
                    trackingNos = new ArrayList<>();
                    index = 1;
                }
                trackingNos.add(orderTracking.getTracking_no());
                ++index;
                ++accIndex;
            } else {
                // 그렇지 않고, 같거나 더 적을 때는.
                trackingNos.add(orderTracking.getTracking_no());
                ++index;
                if (index > orderTrackingList.size()) {
                    trackingSets.add(trackingNos);
                }
            }
        }
        // 50개씩 묶은 데이터를 매 요청에 담아 조회하고 데이터를 구분한다.
        // DHL 배송 상태를 파악하는 JSON 파일 객체를 담고 있다
        JSONObject shipmentCode = new JSONObject();
        try {
            shipmentCode = shipmentStatusCode.getShipmentStatusJSON();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // DB 에서 가져온 데이터와 DHL 데이터와 메핑해주기 위해 필요.
        Map<String, Map<String, String>> jsonMap = convertToJsonMap(orderTrackingList);

        List<DHLTrackingVO> deliveredOrders = new ArrayList<>();
        List<DHLTrackingVO> customIssueOrders = new ArrayList<>();
        List<DHLTrackingVO> otherIssueOrders = new ArrayList<>();
        List<DHLTrackingVO> untrackableOrders = new ArrayList<>();
        List<DHLTrackingVO> delayedOrders = new ArrayList<>();
        List<DHLTrackingVO> returnedOrders = new ArrayList<>();

        for (List<String> trackingSet : trackingSets) {
            DHLTrackingResponseStorage storage = dHLShipmentTrackingService.trackShipments(trackingSet);

            // 반환된 매 운송장 값의 결과.
            for (DHLTrackingResponse response : storage.getResponses()) {
                String shipmentClass = jsonMap.get(response.getTrackingNo()).get("shipment_class");

                // 정상적인 조회가 아닌경우,
                try {
                    if (!response.getStatus().getActionStatus().equals("Success")) {

                        // 해당 값에 대한 오류 표기 하고
                        untrackableOrders.add(new DHLTrackingVO(
                                jsonMap.get(response.getTrackingNo()).get("order_no"),
                                response.getTrackingNo(),
                                shipmentClass));
                        continue;
                    }
                } catch (NullPointerException np) {
                    System.out.println(response.toString());
                }

                // 데이터 중에서 배송 완료가 된 이벤트 코드가 있는지 확인이 필요.
                // DHL 전산의 이유로 인해 이상한 데이터 처리(순서가 이상함 등 )가 있을 수 있음.
                // status.json 에서 정의된 이벤트 코드 유형의 가중치가 가장 높은 값을 가지고 나온다.
                JSONObject priortyLevel = shipmentCode.getJSONObject("priorityLevel");
                JSONObject shipmentEventCode = shipmentCode.getJSONObject("status");

                // int currentPriorityNo = 99;
                // String currentEventCode = "";

                // ShipmentEvent selectedEvent = new ShipmentEvent();
                // for (ShipmentEvent event : response.getShipmentEvents()) {
                // currentEventCode =
                // shipmentEventCode.getJSONObject(event.getEventCode()).getString("hpGroup");

                // if (priortyLevel.getInt(currentEventCode) < currentPriorityNo) {
                // selectedEvent = event;
                // currentPriorityNo = priortyLevel.getInt(currentEventCode);
                // }
                // }
                // events 들 중 배송 완료 값이 있으면 오케이. 없으면 제일 최신 데이터 가져오기
                // "WC", "FD" 는 타 배송사
                ShipmentEvent selectedEvent = null;
                // Shipment on Hold 는 다양한 이유로 발생한다.
                // 해당 이벤트가 3건 이상 발생하면 문제를 보고한다.
                int noOfShipmentOnHold = 0;

                // 배송이 완료 되었으면 완료되었다고 하면 되지만.
                for (ShipmentEvent event : response.getShipmentEvents()) {
                    if (Arrays.asList("BR", "DL", "TP", "DD", "PD", "OK").contains(event.getEventCode())) {
                        selectedEvent = event;
                        break;
                    }
                }
                for (ShipmentEvent currentEvent : response.getShipmentEvents()) {
                    if (currentEvent.getEventCode().equals("OH")) {
                        noOfShipmentOnHold += 1;
                    }
                    if (noOfShipmentOnHold >= 3) {
                        selectedEvent = currentEvent;
                        break;
                    }

                }
                // 만약 배송 완료된 것을 못찾았으면
                if (selectedEvent == null) {
                    selectedEvent = response.getShipmentEvents().get(response.getShipmentEvents().size() - 1);
                }

                // 상황에 따라 담아 처리할 리스트 마련.
                // 한품 내에서 지정한 기준에 의거하여 배송 상태에 따른 각 다른 업무를 처리한다.
                // 위에서 가중치가 가장 높은 Event 를 추출함.
                String eventCase = shipmentEventCode.getJSONObject(selectedEvent.getEventCode())
                        .getString("hpGroup");
                String orderNo = String.valueOf(response.getShipmentDetail().getShipmentReference());

                DHLTrackingVO responseVo = new DHLTrackingVO();
                responseVo.setOrder_no(orderNo);
                responseVo.setTracking_no(response.getTrackingNo());
                responseVo.setShipped_dtime(response.getShipmentDetail().getShippedDate());
                responseVo.setEvent(shipmentEventCode.getJSONObject(selectedEvent.getEventCode()).getString("korDesc"));
                responseVo.setEvent_code(selectedEvent.getEventCode());
                responseVo.setEvent_dtime(String.format("%s %s", selectedEvent.getDate(), selectedEvent.getTime()));
                responseVo.setShipment_class(shipmentClass);
                responseVo.setShipment_issue_type(eventCase);
                responseVo.setOrder_date(jsonMap.get(response.getTrackingNo()).get("order_date"));

                switch (eventCase) {
                    case "delivered":
                        responseVo.setTypeOfIssue("delivered");
                        deliveredOrders.add(responseVo);
                        break;

                    case "returned":
                        responseVo.setTypeOfIssue("other-issue");
                        returnedOrders.add(responseVo);
                        break;

                    case "urgency-customs":
                        responseVo.setEventRemarkDetails(selectedEvent.getFurtherDetails());
                        responseVo.setEventRemarkNextSteps(selectedEvent.getNextSteps());
                        responseVo.setTypeOfIssue("clearance-issue");
                        customIssueOrders.add(responseVo);
                        break;

                    case "urgency-shipment":
                        responseVo.setTypeOfIssue("other-issue");
                        otherIssueOrders.add(responseVo);
                        break;

                    case "refuse":
                        responseVo.setTypeOfIssue("other-issue");
                        otherIssueOrders.add(responseVo);
                        break;

                    case "exception":
                        responseVo.setTypeOfIssue("other-issue");
                        otherIssueOrders.add(responseVo);
                        break;

                    case "clearance-delay":
                        responseVo.setTypeOfIssue("other-issue");
                        otherIssueOrders.add(responseVo);
                        break;
                    // 이하 차후 목적을 위해 남겨놓음.
                    // case "clearance-process":
                    // break;
                    // case "closed":
                    // break;
                    // case "in-transit":
                    // break;
                    // case "shipment-start":
                    // break;

                    // shipped 인 상태에서 아직도 발송을 안한 케이스의 경우 문제로 빠짐.
                    case "shipment-ready":
                        responseVo.setTypeOfIssue("other-issue");
                        otherIssueOrders.add(responseVo);
                        break;
                    // 위의 조건에도 부합하지 않은 건들은 배송 기간을 조회하여 지연여부를 확인함.
                    default:
                        // 무무면 5일 일반은 10일
                        int delayAllowableDays = 10;
                        if (!shipmentClass.equals("regular")) {
                            delayAllowableDays = 5;
                        }

                        if (isDelayedShipment(response.getShipmentEvents(), delayAllowableDays)) {
                            delayedOrders.add(new DHLTrackingVO(
                                    orderNo,
                                    response.getTrackingNo(),
                                    response.getShipmentDetail().getShippedDate(),
                                    shipmentClass));
                        }
                        break;
                }
                this.deliveredOrders = deliveredOrders;
                this.customsIssueOrders = customIssueOrders;
                this.otherIssueOrders = otherIssueOrders;
                this.delayedOrders = delayedOrders;
                this.untrackableOrders = untrackableOrders;
                this.returnedOrders = returnedOrders;
            }
        }

        return new DHLTrackingResult(
                NumberFormat.getNumberInstance(Locale.US).format(deliveredOrders.size()),
                NumberFormat.getNumberInstance(Locale.US).format(customIssueOrders.size()),
                NumberFormat.getNumberInstance(Locale.US).format(otherIssueOrders.size()),
                NumberFormat.getNumberInstance(Locale.US).format(delayedOrders.size()),
                NumberFormat.getNumberInstance(Locale.US).format(untrackableOrders.size()),
                NumberFormat.getNumberInstance(Locale.US).format(returnedOrders.size()),

                NumberFormat.getNumberInstance(Locale.US).format(orderTrackingList.size() -
                        deliveredOrders.size() + customIssueOrders.size() +
                        otherIssueOrders.size() + delayedOrders.size() +
                        untrackableOrders.size() + returnedOrders.size()));
    }

    // DB DATA Processing
    private List<DHLTrackingVO> getTrackableOrders() {
        List<DHLTrackingVO> trackingVOs = new ArrayList<>();
        try {
            // 지금 현재부터 60일 전 까지의 Shipped 데이터를 가져오기.
            String start_dtime = LocalDateTime.now().minusDays(100)
                    .format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));
            String end_dtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_PATTERN));

            Map<String, String> dateRange = new HashMap<>();
            dateRange.put("start_dtime", start_dtime);
            dateRange.put("end_dtime", end_dtime);
            System.out.println(String.format("%s 부터 %s 까지의 기록을 조회합니다.", start_dtime, end_dtime));
            trackingVOs = mapper.getTrackableOrders(dateRange);
            System.out.println(String.valueOf(trackingVOs.size()) + " 개를 조회합니다.");

            return trackingVOs;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    private DHLTrackingVO getOrderDetailByTrackingNo(DHLTrackingVO searcVo) {
        try {
            return mapper.getOrderDetailByTrackingNo(searcVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Map<String, String>> convertToJsonMap(List<DHLTrackingVO> trackingVOs) {
        Map<String, Map<String, String>> jsonMap = new HashMap<>();
        for (DHLTrackingVO trackingVo : trackingVOs) {
            try {
                Map<String, String> childMap = new HashMap<>();
                childMap.put("order_no", trackingVo.getOrder_no());
                childMap.put("order_date", trackingVo.getOrder_date());
                childMap.put("shipment_class", trackingVo.getShipment_class());
                // childMap.put("out_dtime", trackingVo.getOut_dtime());
                jsonMap.put(trackingVo.getTracking_no(), childMap);

            } catch (Exception e) {
                System.out.println(trackingVo + " 문제발생: 아마 중복 값 문제일 거임.");
                e.printStackTrace();
            }
        }
        return jsonMap;
    }
    // DHL DATA Processing

    // Filter the delayed days for the undelivered orders
    // 10 days of working days (2 weeks)
    public boolean isDelayedShipment(List<ShipmentEvent> shipmentEvents, int delayAllowableDays) {
        // Weekend Not Included.
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

            if (days >= delayAllowableDays) {
                // System.out.println(String.valueOf(days) + " 일 지남.");
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
