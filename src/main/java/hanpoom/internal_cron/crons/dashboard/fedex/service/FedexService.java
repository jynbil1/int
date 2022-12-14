package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.shipment.fedex.enumerate.FedexShipmentStatus;
import hanpoom.internal_cron.api.shipment.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.LatestStatusDetail;
import hanpoom.internal_cron.api.shipment.fedex.vo.track.FedexTrackResponse.TrackResult;
import hanpoom.internal_cron.api.slack.SlackAPI;
import hanpoom.internal_cron.crons.dashboard.fedex.mapper.FedexMapper;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.FedexExcelRow;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;
import hanpoom.internal_cron.utility.calendar.CalendarFormatter;
import hanpoom.internal_cron.utility.group.Grouping;

@Service
public class FedexService {
    // Live Slack URL
    private static final String FEDEX_SLACK_ALARM_URL = "https://hooks.slack.com/services/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";

    // Test Slack URL
    // private static final String FEDEX_SLACK_ALARM_URL =
    // "https://hooks.slack.com/services/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";

    @Autowired
    private FedexSpreadSheet fedexSpreadSheet;
    @Autowired
    private FedexTrackManager fedexTrackManager;
    @Autowired
    private SlackAPI slack;

    @Autowired
    private FedexMapper fedexMapper;

    private List<OrderShipment> getShippedFedexOrders() {
        try {
            return fedexMapper.getOrderShipments();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<OrderShipment> getShipments(List<Integer> orderNos) {
        try {
            return fedexMapper.getShipments(orderNos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertDeliveredShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertDeliveredShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertErrorShipments(List<OrderShipment> orders) {
        try {
            fedexMapper.insertErrorShipments(orders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getTrackReportMsg(Map<String, String> workResult) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("***")
                .append(CalendarFormatter.toKoreanDate(LocalDate.now()))
                .append(" ")
                .append(String.valueOf(LocalDateTime.now().getHour()))
                .append("??? Fedex ?????? ??????***\n")
                .append("---------------------------------------------------\n")
                .append("?????? ??????: ")
                .append(workResult.get("delivered"))
                .append(" ???\n\n ?????? ??????: ")
                .append(workResult.get("delayed"))
                .append(" ???\n ?????? ??????: ")
                .append(workResult.get("untrackable"))
                .append(" ???\n ?????? ??????: ")
                .append(workResult.get("beforePickup"))

                // .append(" ???\n\n ?????? ??????: ")
                // .append(workResult.get("returned"))

                .append(" ???\n---------------------------------------------------\n")
                .append("?????????: ")
                .append(workResult.get("inTransit"))
                .append(" ???\n")
                .append("<https://docs.google.com/spreadsheets/d/1G3Y2CWeYveB2KNVRduKTSgFZuOIh7Cb8JQZOO0gBDqw/edit#gid=1386751274|?????? ????????????>");

        return sb.toString();
    }

    public void monitorNReportFedexShipments() {
        // ??? ???????????? ????????? ???.
        int deliveredOrders = 0;
        int delayedOrders = 0;
        int issueOrders = 0;
        int untrackableOrders = 0;
        int returnedOrders = 0;
        int otherIssueOrders = 0;
        int beforePickupOrders = 0;
        int inTransitOrders = 0;

        boolean areShipmentsMonitored = false;

        // 1. ????????? ????????? ??????. -->
        List<OrderShipment> orderShipments = getShippedFedexOrders();
        List<OrderShipment> errorShipments = new ArrayList<>();
        List<OrderShipment> deliveredShipments = new ArrayList<>();

        // ????????? ???????????? ????????? ?????? ???????????? ???????????? ??????.
        if (orderShipments.isEmpty() || orderShipments.size() < 1) {
            return;
        }

        // 1.5. ????????? ????????? ??? ?????? ?????? ????????? 30????????? ????????? ???.
        List<List<OrderShipment>> orderShipmentSets = new Grouping<OrderShipment>().groupByNumberSet(orderShipments,
                30);

        // 2. ?????? ?????? ?????? (tss -> Trackable Shipment Set)
        HashSet<String> trackingNos = new HashSet<>();

        try {

            for (List<OrderShipment> tss : orderShipmentSets) {
                // tss ???????????? ????????? ????????? ???????????? List<String> ?????? ?????????.
                trackingNos = new HashSet<>(tss.stream().map(obj -> obj.getTrackingNo()).collect(Collectors.toList()));

                List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(trackingNos, true);
                trackingNos = new HashSet<>();

                // 2.1 ?????? ?????? ??????

                for (FedexTrackResponse response : responses) {
                    String issueType = "";
                    String event = "";
                    String eventCode = "";

                    LocalDateTime eventDate = LocalDateTime.now();
                    TrackResult result = response.getTrackResults().get(0);

//                     fixMe: if orderStatus is not valid in the future test / production.
//                    String orderStatus = tss.stream().filter(el -> el.getTrackingNo().equals(response.getTrackingNumber())).findFirst().get().getOrderStatus();

                    OrderShipment selectedOrder = orderShipments
                            .stream()
                            .filter(key -> response.getTrackingNumber().equals(key.getTrackingNo()))
                            .findFirst()
                            .get();

                    if (fedexTrackManager.isDelivered(result)) {
                        ++deliveredOrders;
                        selectedOrder.setShippedDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.SHIPPED));
                        selectedOrder.setEventDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.DELIVERED));

                        selectedOrder.setEvent("????????????");
                        selectedOrder.setEventCode("OK");
                        areShipmentsMonitored = true;
                        deliveredShipments.add(selectedOrder);
                    } else {
                        if (fedexTrackManager.isDelayed(result)) {
                            LatestStatusDetail latestEvent = fedexTrackManager.getRecentEvent(result);
                            issueType = "??????";
                            eventCode = latestEvent.getCode();
                            event = latestEvent.getDescription();

                            ++delayedOrders;
                            areShipmentsMonitored = true;
                        } else if (fedexTrackManager.isBeforePickUp(result)) {
                            ++beforePickupOrders;

                            // } else if (fedexTrackManager.isProblematic(result)) {
                            // issueType = "??????";
                            // eventCode = "PRB";
                            // // event =
                            // //
                            // result.getDateAndTimes().stream().sorted(Comparator.comparing(DateAndTime::getDateTime).reversed()).findFirst().get().get;

                            // areShipmentsMonitored = true;
                            // ++issueOrders;
                            // eventDate = fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.);

                            // } else if (fedexTrackManager.isReturned(result)) {
                            // issueType = "??????";
                            // eventCode = "DLY";
                            // event = "Shipment is delayed than expected.";

                            // areShipmentsMonitored = true;
                            // ++returnedOrders;
                        } else if (fedexTrackManager.isNotFound(result)) {
                            issueType = "?????? ??? ??????";
                            eventCode = "NF";
                            event = "Tracking Number is not Found.";

                            areShipmentsMonitored = true;
                            ++untrackableOrders;
                        } else {
                            ++inTransitOrders;
                            continue;
                        }

                        selectedOrder.setShippedDate(
                                fedexTrackManager.getEventDateTime(result, FedexShipmentStatus.SHIPPED));
                        selectedOrder.setEvent(event);
                        selectedOrder.setEventCode(eventCode);
                        selectedOrder.setIssueType(issueType);
                        selectedOrder.setEventDate(eventDate);

                        errorShipments.add(selectedOrder);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. ?????? ??????/????????? ??? DB ??????
        if (!deliveredShipments.isEmpty()) {
            insertDeliveredShipments(deliveredShipments);
        }

        // 4. ????????? ??????
        if (!errorShipments.isEmpty()) {
            fedexSpreadSheet.insertIntoFedexSheet(errorShipments);
            insertErrorShipments(errorShipments);
        }

        // 5. ?????? ??????.
        Map<String, String> workResult = Map.of(
                "delivered", NumberFormat.getInstance().format(deliveredOrders),
                "delayed", NumberFormat.getInstance().format(delayedOrders),
                "untrackable", NumberFormat.getInstance().format(untrackableOrders),
                "beforePickup", NumberFormat.getInstance().format(beforePickupOrders),
                // "others", NumberFormat.getInstance().format(otherIssueOrders),
                // "returned", NumberFormat.getInstance().format(returnedOrders),
                "inTransit", NumberFormat.getInstance().format(inTransitOrders));

        try {
            // ??????????????? '?????????' ?????? ????????? ???????????? ????????? ????????????.
            if (areShipmentsMonitored) {
                slack.sendMessage(getTrackReportMsg(workResult), FEDEX_SLACK_ALARM_URL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reMonitorFedexIssueShipments() {
        // 1. ?????? ??????_CX - Fedex ??? ?????? ????????? ?????? ????????????.
        List<FedexExcelRow> rows = new ArrayList<>();
        try {
            rows = fedexSpreadSheet.readUnresolvedShipmentExcel();
            if (rows.isEmpty() || rows.size() < 1) {
                System.out.println("????????? ?????? ????????????.");
                return;
            }
        } catch (NullPointerException npe) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. ?????? ????????? ?????? DB ??? ????????? ????????? ????????? ???????????? ????????????.
        List<OrderShipment> orderShipments = getShipments(
                rows.stream()
                        .map(key -> key.getOrderNo().getValue())
                        .collect(Collectors.toList()));

        // ????????? ????????? ????????? ?????????, ?????? ??????????????? ???????????? ????????? ?????????.
        List<FedexExcelRow> updatableRows = new ArrayList<>();
        for (FedexExcelRow row : rows) {
            // ????????? ????????? ?????????, ??????????????? ????????? ????????? true
            try {
                OrderShipment shipment = orderShipments.stream()
                        .filter(obj -> !obj.getTrackingNo().equals(row.getTrackingNo().getValue()) &&
                                obj.getOrderNo() == row.getOrderNo().getValue())
                        .findFirst().get();

                row.getTrackingNo().setValue(shipment.getTrackingNo());
                updatableRows.add(row);
            } catch (NoSuchElementException nee) {
                continue;
            }
        }

        // 3. ???????????? ?????? ??????, DB ?????? ?????? ?????? ????????? ????????????.
        if (!updatableRows.isEmpty()) {
            fedexSpreadSheet.insertNewTrackingNumbers(updatableRows);
        }

        // 4. ????????? ????????? ???????????? Fedex ????????? ????????? ??????.
        List<OrderShipment> deliveredShipments = new ArrayList<>();
        if (!orderShipments.isEmpty() && orderShipments.size() > 0) {
            List<List<OrderShipment>> shipmentsSet = new Grouping<OrderShipment>().groupByNumberSet(orderShipments, 30);

            for (List<OrderShipment> shipments : shipmentsSet) {
                // shipments ??? ?????? ?????? ????????? ????????? ???????????? ???????????? ?????? ????????? ?????????.
                List<FedexTrackResponse> responses = fedexTrackManager.trackMultipleShipments(new HashSet<String>(
                        shipments
                                .stream()
                                .map(obj -> obj.getTrackingNo())
                                .collect(Collectors.toList())),
                        false);

                for (FedexTrackResponse response : responses) {
                    if (fedexTrackManager.isDelivered(response.getTrackResults().get(0))) {
                        // 5. ????????? ?????????
                        // 5.1. ???????????????????????? ?????? ????????? ??????,
                        try {
                            FedexExcelRow selectedRow = rows
                                    .stream()
                                    .filter(object -> object.getTrackingNo().getValue()
                                            .equals(response.getTrackingNumber()))
                                    .findFirst()
                                    .get();

                            selectedRow.getIsCompleted().setValue(true);
                            selectedRow.getDeliveredAt().setValue(
                                    (fedexTrackManager.getEventDateTime(
                                            response.getTrackResults().get(0), FedexShipmentStatus.DELIVERED).format(
                                                    DateTimeFormatter.ofPattern(CalendarFormatter.DATETIME))));
                            fedexSpreadSheet.checkNRecordShipment(selectedRow);

                            for (OrderShipment shipment : shipments) {
                                if (response.getTrackingNumber().equals(shipment.getTrackingNo())) {

                                    TrackResult shipResponse = response.getTrackResults().get(0);
                                    shipment.setEvent("????????????");
                                    shipment.setEventCode("OK");

                                    shipment.setShippedDate(
                                            fedexTrackManager.getEventDateTime(shipResponse,
                                                    FedexShipmentStatus.SHIPPED));
                                    shipment.setEventDate(
                                            fedexTrackManager.getEventDateTime(shipResponse,
                                                    FedexShipmentStatus.DELIVERED));
                                    deliveredShipments.add(shipment);
                                }
                            }

                        } catch (NoSuchElementException nee) {
                            continue;
                        }

                    }
                }
            }
        }

        // 5.2. us_ca_a_wh_delivered ??? ?????? ????????????.
        if (!deliveredShipments.isEmpty() && deliveredShipments.size() > 0) {
            insertDeliveredShipments(deliveredShipments);
        }
    }
}
