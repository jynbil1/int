package hanpoom.internal_cron.crons.dashboard.dhl.service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Append;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.shipment.dhl.config.DHLShipmentStatusCode;
import hanpoom.internal_cron.api.shipment.dhl.service.DHLShipmentTrackingService;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackResponse;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackResponse.EventRemark;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackResponse.ServiceEvent;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackResponse.ShipmentEventItem;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingResult;
import hanpoom.internal_cron.crons.dashboard.dhl.vo.DHLTrackingVO;

@Service
public class DHLShipmentHandler {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DHLShipmentStatusCode shipmentStatusCode;
    private DHLService dhlService;
    private DHLShipmentTrackingService dhlTrackingService;
    private SlackAPI slack;

    public DHLShipmentHandler(DHLShipmentStatusCode shipmentStatusCode,
            DHLService dhlService,
            DHLShipmentTrackingService dhlTrackingService,
            SlackAPI slack) {
        this.shipmentStatusCode = shipmentStatusCode;
        this.dhlService = dhlService;
        this.dhlTrackingService = dhlTrackingService;
        this.slack = slack;
    }

    public DHLTrackingVO filterShipment(DHLTrackingVO searchVo) {
        // Tracking No 를 조회해서 값이 하나 이상이 나올 수 있기 때문에 대비해야 함.

        DHLTrackingVO trackingVo = dhlService.getOrderDetailByTrackingNo(searchVo);
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

        DHLTrackResponse response = dhlTrackingService.trackSingleShipment(trackingVo.getTracking_no());

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

        ShipmentEventItem selectedEvent = analyzeShipmentHistory(response, shipmentEventCode);

        // 상황에 따라 담아 처리할 리스트 마련.
        // 한품 내에서 지정한 기준에 의거하여 배송 상태에 따른 각 다른 업무를 처리한다.
        // 위에서 가중치가 가장 높은 Event 를 추출함.
        String eventCode = selectedEvent.getServiceEvent().getEventCode();
        String eventCase = shipmentEventCode.getJSONObject(eventCode)
                .getString("hpGroup");

        String orderNo = String.valueOf(response.getShipmentInfo().getShipperReference().getReferenceId());
        DHLTrackingVO responseVo = new DHLTrackingVO();

        responseVo.setOrder_no(orderNo);
        responseVo.setTracking_no(response.getTrackingNumber());
        responseVo.setShipped_dtime(selectedEvent.getDate() + " " + selectedEvent.getTime());
        responseVo.setEvent(shipmentEventCode.getJSONObject(eventCode).getString("korDesc"));
        responseVo.setEvent_code(eventCode);
        responseVo.setEvent_dtime(String.format("%s %s", selectedEvent.getDate(), selectedEvent.getTime()));
        responseVo.setShipment_class(trackingVo.getShipment_class());
        responseVo.setOrder_date(trackingVo.getOrder_date());
        responseVo.setShipment_issue_type(eventCase);

        switch (eventCase) {
            case "delivered":
                responseVo.setTypeOfIssue("delivered");

                return responseVo;

            case "returned":
                responseVo.setTypeOfIssue("other-issue");
                return responseVo;

            case "urgency-customs":
                responseVo.setEventRemarkDetails(selectedEvent.getEventRemark().getFurtherDetail());
                responseVo.setEventRemarkNextSteps(selectedEvent.getEventRemark().getNextStep());
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

                if (isDelayedShipment(response.getShipmentInfo().getShipmentEvent().getShipmentEventItems(),
                        delayAllowableDays)) {
                    return new DHLTrackingVO(
                            orderNo,
                            response.getTrackingNumber(),
                            trackingVo.getOrder_date(),
                            selectedEvent.getDate() + " " + selectedEvent.getTime(),
                            trackingVo.getShipment_class());
                }
                break;
        }
        return null;
    }

    public DHLTrackingResult filterShipments() {

        List<DHLTrackingVO> orderTrackingList = dhlService.getTrackableOrders();
        List<List<String>> trackingSets = new ArrayList<>();
        List<String> trackingNos = new ArrayList<>();

        int limit = 50;
        int index = 1;
        int accIndex = 1;
        // 한번에 많은 데이터를 요청하면 안되므로, 50개씩 묶는작업이다.
        System.out.println(orderTrackingList.size());
        for (DHLTrackingVO orderTracking : orderTrackingList) {
            // 처리해야 할 건이 한번에 처리할 건보다 더 많을 경우,
            if (orderTrackingList.size() > limit) {
                trackingNos.add(orderTracking.getTracking_no());
                if (index == limit || orderTrackingList.size() == accIndex) {
                    trackingSets.add(trackingNos);
                    trackingNos = new ArrayList<>();
                    index = 0;
                }
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
            List<DHLTrackResponse> responses = dhlTrackingService
                    .trackMultipleShipments(new HashSet<>(trackingSet));
            if (responses == null) {
                continue;
            }
            // 반환된 매 운송장 값의 결과.
            for (DHLTrackResponse response : responses) {
                String shipmentClass = jsonMap.get(response.getTrackingNumber()).get("shipment_class");

                // 정상적인 조회가 아닌경우,
                try {
                    if (!response.getStatus().getActionStatus().equals("Success")) {

                        // 해당 값에 대한 오류 표기 하고
                        untrackableOrders.add(new DHLTrackingVO(
                                jsonMap.get(response.getTrackingNumber()).get("order_no"),
                                response.getTrackingNumber(),
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

                // Shipment on Hold 는 다양한 이유로 발생한다.
                // 해당 이벤트가 3건 이상 발생하면 문제를 보고한다.
                // nullpointer 가 나오면, 생성만하고 조회가 되지 않는 건임.
                ShipmentEventItem selectedEvent = new ShipmentEventItem();
                try {
                    selectedEvent = analyzeShipmentHistory(response, shipmentEventCode);
                } catch (NullPointerException npe) {
                    System.out.println("------------------------");
                    String orderNo = response.getShipmentInfo().getShipperReference().getReferenceId();
                    String trackingNo = response.getTrackingNumber();

                    String message = new StringBuilder()
                            .append("아래 주문 운송장은 유효하지 않습니다.\n")
                            .append("전산 발송 처리는 되었으나, 실물은 발송되지 않은 상태일 수 있습니다.\n")
                            .append("================================================\n")
                            .append("주문번호: ")
                            .append(orderNo)
                            .append(String.format(
                                    " <https://www.hanpoom.com/wp-admin/post.php?post=%s&action=edit|조회하기>", orderNo))
                            .append("\n")

                            .append("운송장번호:")
                            .append(trackingNo)
                            .append(String.format(
                                    " <https://www.dhl.com/kr-en/home/tracking/tracking-express.html?submit=1&tracking-id=%s|운송사조회>",
                                    trackingNo))
                            .append("\n")

                            .append("운송사: DHL")
                            .toString();

                    slack.sendMessage(message);
                    System.out.println(response.toString());
                    continue;
                }

                // 상황에 따라 담아 처리할 리스트 마련.
                // 한품 내에서 지정한 기준에 의거하여 배송 상태에 따른 각 다른 업무를 처리한다.
                // 위에서 가중치가 가장 높은 Event 를 추출함.

                String eventCode = selectedEvent.getServiceEvent().getEventCode();
                String eventCase = shipmentEventCode.getJSONObject(eventCode)
                        .getString("hpGroup");

                String orderNo = null;
                // 운송장 레퍼런스 입력하는 곳에 항상 절대적으로 주문 번호가 들어가는 것이 아니기 때문에 문제가 발생한다.
                // 그리하여 아래와 같이 DB 의 값을 먼저 우선시 하고, 정상적이지 않을 경우 운송장에 적힌 Reference 값을 사용하는데,
                // 반환된 값 또한 정상적 주문번호가 아닐 경우, 해당 반복문을 건너뛴다.
                try {
                    orderNo = jsonMap.get(response.getTrackingNumber()).get("order_no");
                } catch (NullPointerException npe) {
                    orderNo = String.valueOf(response.getShipmentInfo().getShipperReference().getReferenceId());
                    if (String.valueOf(orderNo).length() > 9) {
                        System.out.println("정상적인 주문 번호를 찾을 수 없습니다.");
                        System.out.println(response.toString());
                        continue;
                    }
                }

                DHLTrackingVO responseVo = new DHLTrackingVO();
                responseVo.setOrder_no(orderNo);
                responseVo.setTracking_no(response.getTrackingNumber());
                responseVo.setShipped_dtime(selectedEvent.getDate() + " " + selectedEvent.getTime());
                responseVo.setEvent(shipmentEventCode.getJSONObject(eventCode).getString("korDesc"));
                responseVo.setEvent_code(eventCode);
                responseVo.setEvent_dtime(String.format("%s %s", selectedEvent.getDate(), selectedEvent.getTime()));
                responseVo.setShipment_class(shipmentClass);
                responseVo.setShipment_issue_type(eventCase);
                responseVo.setOrder_date(jsonMap.get(response.getTrackingNumber()).get("order_date"));

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
                        EventRemark remark = selectedEvent.getEventRemark();

                        responseVo.setEventRemarkDetails(remark == null ? "" : remark.getFurtherDetail());
                        responseVo.setEventRemarkNextSteps(remark == null ? "" : remark.getNextStep());
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
                        // System.out.println(jsonMap.get(response.getTrackingNo()).toString());
                        int delayAllowableDays = 10;
                        if (!shipmentClass.equals("regular")) {
                            delayAllowableDays = 5;
                        }

                        if (isDelayedShipment(response.getShipmentInfo().getShipmentEvent().getShipmentEventItems(),
                                delayAllowableDays)) {
                            // responseVo.setOrder_date(jsonMap.get(response.getTrackingNo()).get("order_date"));

                            delayedOrders.add(new DHLTrackingVO(
                                    orderNo,
                                    response.getTrackingNumber(),
                                    jsonMap.get(response.getTrackingNumber()).get("order_date"),
                                    selectedEvent.getDate() + " " + selectedEvent.getTime(),
                                    shipmentClass));
                        }
                        break;
                }
            }
        }

        int intTransitOrders = orderTrackingList.size() -
                deliveredOrders.size() - customIssueOrders.size() -
                otherIssueOrders.size() - delayedOrders.size() -
                untrackableOrders.size() - returnedOrders.size();

        return DHLTrackingResult.builder()
                .deliveredOrders(deliveredOrders)
                .customIssueOrders(customIssueOrders)
                .otherIssueOrders(otherIssueOrders)
                .delayedOrders(delayedOrders)
                .untrackableOrders(untrackableOrders)
                .returnedOrders(returnedOrders)
                .totalInTransit(
                        DHLTrackingResult.getMoneyFormat(intTransitOrders))
                .build();

    }

    private ShipmentEventItem analyzeShipmentHistory(DHLTrackResponse response, JSONObject shipmentEventCode) {
        int noOfShipmentOnHold = 0;
        int noOfCustomstatusUpdated = 0;

        List<ShipmentEventItem> eventItems = response.getShipmentInfo().getShipmentEvent().getShipmentEventItems();

        // 배송이 완료 되었으면 완료되었다고 하면 되지만.
        for (ShipmentEventItem event : eventItems) {
            if (Arrays.asList("BR", "DL", "DD", "PD", "OK").contains(event.getServiceEvent().getEventCode())) {
                return event;
            }
        }
        // Shipment On hold 는 한 두번 나오면 괜찮지만, 계속 나오게 되면 문제인 건임.
        for (ShipmentEventItem currentEvent : eventItems) {
            if (currentEvent.getServiceEvent().getEventCode().equals("OH")) {
                noOfShipmentOnHold += 1;
            }
            if (noOfShipmentOnHold >= 3) {
                return currentEvent;

            }
        }

        // Customs Status Updated 는 한 두번 나오면 괜찮지만, 계속 나오게 되면 문제인 건임.
        // 다만 이 경우에는, 연속적으로 3번 이상이 나올 경우라는 조건이 추가가 되어야 함.
        for (ShipmentEventItem currentEvent : eventItems) {
            if (currentEvent.getServiceEvent().getEventCode().equals("RR")) {
                noOfCustomstatusUpdated += 1;
            } else if (!currentEvent.getServiceEvent().getEventCode().equals("RR") && noOfCustomstatusUpdated > 0) {
                // 값이 RR 이 아니고 찾은 CustomstatusUpdated 건이 0 보다 클 때
                noOfCustomstatusUpdated -= 1;
            }
            if (noOfCustomstatusUpdated >= 3) {
                return currentEvent;
            }
        }

        // 배송이 완료된 것도 없고, 통관문제인게 없으나, 제일 최신 데이터가 Shipment on Hold 일 경우에는,
        // code 를 ITH 로 변경한다.
        // 그렇지 않으면 그냥 제일 최신 배송 상태를 가져온다.
        ShipmentEventItem selectedEvent = response.getShipmentInfo().getShipmentEvent().getShipmentEventItems()
                .get(response.getShipmentInfo().getShipmentEvent().getShipmentEventItems().size() - 1);

        ServiceEvent event = selectedEvent.getServiceEvent();
        if (event.getEventCode().equals("OH")) {
            event.setEventCode("ITH");
            event.setDescription(shipmentEventCode.getJSONObject("ITH").getString("korDesc"));
        } else if (event.getEventCode().equals("RR")) {
            event.setEventCode("CUS");
            event.setDescription(shipmentEventCode.getJSONObject("CUS").getString("korDesc"));
        }
        selectedEvent.setServiceEvent(event);
        return selectedEvent;
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
    public boolean isDelayedShipment(List<ShipmentEventItem> shipmentEvents, int delayAllowableDays) {
        // Weekend Not Included.
        // ShipmentEvent event = new ShipmentEvent();
        ShipmentEventItem event = new ShipmentEventItem();
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

}
