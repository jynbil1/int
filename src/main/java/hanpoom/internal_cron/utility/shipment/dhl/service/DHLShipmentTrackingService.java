package hanpoom.internal_cron.utility.shipment.dhl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.shipment.dhl.config.MyDHLClient;
import hanpoom.internal_cron.utility.shipment.dhl.field.DHLAPIType;
import hanpoom.internal_cron.utility.shipment.dhl.vo.request.DHLTrackingRequest;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponse;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.DHLTrackingResponseStorage;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Consignee;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.PieceDetail;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ServiceArea;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentDetail;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.ShipmentEvent;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Shipper;
import hanpoom.internal_cron.utility.shipment.dhl.vo.response.element.Status;

@Service
public class DHLShipmentTrackingService implements DHLAPI {

    // • ALL_CHECKPOINTS, populates all the customer visible checkpoints available
    // in the response
    // message.
    // • LAST_CHECKPOINT_ONLY, this value is used when only the last checkpoint
    // details are required.
    // • ADVANCE_SHIPMENT, to support advance shipment query. When this value is
    // used, only
    // shipment details information gets populated in response message. Checkpoint
    // details are not
    // populated for Advance Shipment.
    // • BBX_CHILDREN, when the Mother AWB number is provided in the request
    // message, the
    // shipment details and checkpoints of mother AWB with its children’s AWB
    // numbers will be
    // returned. However, there will be no checkpoint details for the childrens AWB
    // number provided.
    // • SHIPMENT_DETAILS_ONLY, only shipment details are provided in the response
    // message. No
    // checkpoint details are populated.

    // • B = Both Piece and Shipment Details (recommended value)
    // • S = Shipment Details Only
    // • P = Piece Details Only.

    private MyDHLClient client;

    public DHLShipmentTrackingService(MyDHLClient client) {
        this.client = client;
    }

    // Recommended No => 50
    // Upto 70 has taken around 10 secs
    public DHLTrackingResponse trackShipment(String trackingNo) {
        // 운송장 한개를 조회해도 두개이상이 나올 수 있음...
        // 원인은 DHL 측의 운송장에 대한 데이터 처리 오류,
        // 우리 운송장이 아닌거랑 같이 나오는데.
        // 그걸 deserialize 메소드에서 분류해줌.
        DHLTrackingRequest request = new DHLTrackingRequest(trackingNo);
        String requestJson = request.getValidatedJSONRequest().toString();

        JSONObject responseJson = callAPI(requestJson);
        JSONArray responseJsonArray = responseJson.optJSONArray("ArrayOfAWBInfoItem");
        if (responseJsonArray == null) {
            return deserializeJsonToPojo(responseJson.getJSONObject("ArrayOfAWBInfoItem"));
        } else {
            DHLTrackingResponse response = new DHLTrackingResponse();
            for (int i = 0; i < responseJsonArray.length(); ++i) {
                response = deserializeJsonToPojo(responseJsonArray.getJSONObject(i));

                if (response == null) {
                    continue;
                } else {
                    break;
                }
            }
            return response;
        }
    }

    public DHLTrackingResponseStorage trackShipments(List<String> trackingNos) {
        DHLTrackingRequest request = new DHLTrackingRequest(trackingNos);
        String requestJson = request.getValidatedJSONRequest().toString();
        JSONObject JsonResponse = new JSONObject();
        JSONArray responseJsonArray = new JSONArray();
        try {
            JsonResponse = callAPI(requestJson);
            responseJsonArray = JsonResponse.optJSONArray("ArrayOfAWBInfoItem");
            if (responseJsonArray == null) {
                return null;
            }
        } catch (Exception e) {
            System.out.println("-------------------------");
            System.out.println(e.getMessage());
            System.out.println("ArrayOfAWBInfoItem 를 못찾는 건 발생: ");
            System.out.println(JsonResponse.toString());
            // jsone.printStackTrace();
            System.out.println("-------------------------");
            return null;
        }

        List<DHLTrackingResponse> responses = new ArrayList<>();
        DHLTrackingResponse response = new DHLTrackingResponse();
        for (int index = 0; index < responseJsonArray.length(); ++index) {
            try {
                response = deserializeJsonToPojo(responseJsonArray.getJSONObject(index));
                if (response == null) {
                    continue;
                } else {
                    responses.add(response);
                }
            } catch (JSONException jsone) {
                System.out.println("-------------------------");
                System.out.println("ArrayOfAWBInfoItem 를 못찾는 건 발생: ");

                System.out.println(responseJsonArray.getJSONObject(index).toString());
                // jsone.printStackTrace();
                System.out.println("-------------------------");
                continue;

            }
        }
        return new DHLTrackingResponseStorage(responses);
    }

    private DHLTrackingResponse deserializeJsonToPojo(JSONObject jsonObject) {
        // 운송장 한 건당 처리를 함.

        DHLTrackingResponse response = new DHLTrackingResponse();
        // 운송장은 값이 없어도 잘 나옴.
        // AWBNumber
        response.setTrackingNo(String.valueOf(jsonObject.optLong("AWBNumber")));
        Status status = new Status();

        // 조회한 운송장의 결과값이 제대로 반환을 했는지부터 검사해야함.
        JSONObject responseStatus = jsonObject.getJSONObject("Status");

        if (!responseStatus.optString("Condition").isBlank()) {
            JSONObject responseCondition = responseStatus.getJSONObject("Condition")
                    .optJSONObject("ArrayOfConditionItem");

            status.setStatus(responseStatus.getString("ActionStatus"),
                    String
                            .valueOf(responseCondition.optInt("ConditionCode")),
                    String
                            .valueOf(responseCondition.optString("ConditionData")));

            if (status.getConditionCode().equals("101")) {
                response.setStatus(status);
                return response;
            }
        } else {
            status.setStatus(responseStatus.getString("ActionStatus"));
        }
        ;

        // 맛탱이간 주문건이 있을 수 있으니 이걸로 구분할 것.
        // 우리가 보낸게 아닌 건들도 집계가 되는 아이러니 한 상황
        String shipperName = jsonObject.getJSONObject("ShipmentInfo").getString("ShipperName");
        boolean existsShipmentEvent = jsonObject.getJSONObject("ShipmentInfo").optString("ShipmentEvent").isBlank()
                ? false
                : true;

        if (!shipperName.toUpperCase().contains("HANPOOM") &
                !shipperName.toUpperCase().contains("KRACCESS") &
                !shipperName.toUpperCase().contains("UNITEDBOARDER") &
                !existsShipmentEvent) {

            return null;
        }

        Consignee consignee = new Consignee();
        Shipper shipper = new Shipper();
        PieceDetail pieceDetail = new PieceDetail();
        ServiceArea serviceArea = new ServiceArea();
        List<ShipmentEvent> shipmentEvents = new ArrayList<>();
        ShipmentDetail shipmentDetail = new ShipmentDetail();

        // Shipment Info -> Service Area
        JSONObject shipmentInfo = jsonObject.getJSONObject("ShipmentInfo");
        JSONObject origin = shipmentInfo.getJSONObject("OriginServiceArea");
        serviceArea.setOriginArea(origin.getString("ServiceAreaCode"),
                origin.getString("Description"),
                origin.optString("FacilityCode"));

        JSONObject destination = shipmentInfo.getJSONObject("DestinationServiceArea");
        serviceArea.setDestinationArea(destination.getString("ServiceAreaCode"),
                destination.getString("Description"),
                destination.optString("FacilityCode"));

        // Get Shipped Date...
        // Shipment Info -> Shipment Events
        // Will be resused for shipment Events below
        // Shipment 가 없으면 Piece 이벤트로 하자...
        JSONArray shipmentEventsObj = new JSONArray();
        if (shipmentInfo.optJSONObject("ShipmentEvent") != null) {
            shipmentEventsObj = shipmentInfo.getJSONObject("ShipmentEvent")
                    .optJSONArray("ArrayOfShipmentEventItem");
            if (shipmentInfo.getJSONObject("ShipmentEvent")
                    .optJSONArray("ArrayOfShipmentEventItem") == null) {
                shipmentEventsObj = new JSONArray().put(shipmentInfo.getJSONObject("ShipmentEvent")
                        .optJSONObject("ArrayOfShipmentEventItem"));
            } else {
                shipmentEventsObj = shipmentInfo.getJSONObject("ShipmentEvent")
                        .getJSONArray("ArrayOfShipmentEventItem");
            }
        } else {

            if (jsonObject.getJSONObject("Pieces").getJSONObject("PieceInfo")
                    .getJSONObject("ArrayOfPieceInfoItem").optJSONObject("PieceEvent") == null) {
                // 운송장이 생성만 된 상태. -- 배송된 건들에 대해서만 확인하는건데 운송장 처리 흐름이 없는 것도 문제임.
                status.setStatus("Untrackable");
                shipmentDetail.setShipmentReference(shipmentInfo.getJSONObject("ShipperReference")
                        .optInt("ReferenceID"));
                response.setShipmentDetail(shipmentDetail);
                response.setStatus(status);
                return response;
            } else {
                shipmentEventsObj = jsonObject.getJSONObject("Pieces").getJSONObject("PieceInfo")
                        .getJSONObject("ArrayOfPieceInfoItem").getJSONObject("PieceEvent")
                        .getJSONArray("ArrayOfPieceEventItem");
            }
        }

        // Find the first event of the shipment except...
        // the event code that indicates the preparation of the shipment which hasn't
        // endorsed to DHL.
        String shippedDate = "";
        List<String> nonProcessables = Arrays.asList("PDR", "SDR", "SA");
        for (int index = 0; index < shipmentEventsObj.length(); ++index) {
            if (nonProcessables.contains(shipmentEventsObj.getJSONObject(index)
                    .getJSONObject("ServiceEvent").getString("EventCode"))) {
                continue;
            } else {
                shippedDate = String.format("%s %s",
                        shipmentEventsObj.getJSONObject(index).getString("Date"),
                        shipmentEventsObj.getJSONObject(index).getString("Time"));
                break;
            }
        }

        // Shipment Info -> Shipment Detail
        shipmentDetail.setShipmentDetail(shipmentInfo.getInt("Pieces"),
                shipmentInfo.optFloat("Weight"),
                shipmentInfo.getString("WeightUnit"),
                shipmentInfo.optString("ServiceType"),
                shipmentInfo.optString("ShipmentDescription"),
                shipmentInfo.optJSONObject("ShipperReference").optInt("ReferenceID"),
                shippedDate);

        // Shipment Info -> Shipment Detail -> Shipper
        JSONObject shipperObj = shipmentInfo.getJSONObject("Shipper");
        shipper.setShipperDetail(shipmentInfo.getString("ShipperName"),
                shipperObj.optString("City"), shipperObj.optString("Suburb"),
                shipperObj.optString("StateOrProvinceCode"),
                shipperObj.optString("PostalCode"), shipperObj.optString("CountryCode"));

        // Shipment Info -> Shipment Detail -> Consignee
        JSONObject consigneeObj = shipmentInfo.getJSONObject("Consignee");
        consignee.setConsigneeDetail(shipmentInfo.optString("ConsigneeName"),
                consigneeObj.optString("City"),
                consigneeObj.optString("Suburb"),
                consigneeObj.optString("StateOrProvinceCode"),
                consigneeObj.optString("PostalCode"), consigneeObj.optString("CountryCode"));

        // Recycle the variable declared above for the shipmentEvents

        JSONObject indexedJson = new JSONObject();
        boolean isSignatoryAdded = false;
        for (int index = 0; index < shipmentEventsObj.length(); ++index) {
            ShipmentEvent shipmentEvent = new ShipmentEvent();
            indexedJson = shipmentEventsObj.getJSONObject(index);
            if (!isSignatoryAdded && indexedJson.optString("Signatory").strip().length() > 2) {
                shipmentDetail.setSignatory(indexedJson.optString("Signatory"));
                isSignatoryAdded = true;
            }
            JSONObject eventRemarks = indexedJson.optJSONObject("EventRemarks");

            shipmentEvent.setShipmentEventDetail(
                    indexedJson.getString("Date"), indexedJson.getString("Time"),
                    indexedJson.getJSONObject("ServiceEvent").getString("EventCode"),
                    indexedJson.getJSONObject("ServiceEvent").getString("Description"),
                    indexedJson.getJSONObject("ServiceArea").getString("ServiceAreaCode"),
                    indexedJson.getJSONObject("ServiceArea").getString("Description"),
                    eventRemarks != null ? eventRemarks.optString("FurtherDetails") : "",
                    eventRemarks != null ? eventRemarks.optString("NextSteps") : "");

            shipmentEvents.add(shipmentEvent);
        }

        JSONObject pieceDetails = jsonObject.getJSONObject("Pieces").getJSONObject("PieceInfo")
                .getJSONObject("ArrayOfPieceInfoItem").getJSONObject("PieceDetails");
        // PieceDetails

        pieceDetail.setPieceDetail(
                pieceDetails.getString("LicensePlate"),
                pieceDetails.getInt("PieceNumber"),

                pieceDetails.optFloat("ActualDepth"),
                pieceDetails.optFloat("ActualWidth"),
                pieceDetails.optFloat("ActualHeight"),
                pieceDetails.optFloat("ActualWeight"),

                pieceDetails.optFloat("Depth"),
                pieceDetails.optFloat("Width"),
                pieceDetails.optFloat("Height"),
                pieceDetails.optFloat("Weight"),

                pieceDetails.optFloat("DimWeight"),
                pieceDetails.getString("WeightUnit"));

        response.setConsignee(consignee);
        response.setShipper(shipper);
        response.setPieceDetail(pieceDetail);
        response.setServiceArea(serviceArea);
        response.setShipmentDetail(shipmentDetail);
        response.setShipmentEvents(shipmentEvents);
        response.setStatus(status);

        return response;
    }

    @Override
    public JSONObject callAPI(String requestJson) {
        HttpURLConnection con = null;
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = null;

        String responseData = null;

        try {
            URL url = new URL(client.getUrl() + DHLAPIType.SHIPMENT_TRACKING);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setDoOutput(true);
            con.setDoInput(true);

            String userPass = String.format("%s:%s", client.getUsername(),
                    client.getPassword());
            String authHeader = String.format("Basic %s",
                    Base64.getEncoder().encodeToString(userPass.getBytes()));

            con.setRequestProperty("Authorization", authHeader);

            try {
                byte[] request = requestJson.getBytes("utf-8");
                // 값을 요청.
                con.getOutputStream().write(request, 0, request.length);

                int responseCode = con.getResponseCode();
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                sb = new StringBuffer();

                if (responseCode == 200) {
                    // 성공적으로 반환을 받았다면,

                    while ((responseData = br.readLine()) != null) {
                        sb.append(responseData);
                    }
                    JSONObject jsonObject = new JSONObject(sb.toString())
                            .getJSONObject("trackShipmentRequestResponse")
                            .getJSONObject("trackingResponse")
                            .getJSONObject("TrackingResponse")
                            .getJSONObject("AWBInfo");
                    try {
                        return jsonObject;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    while ((responseData = br.readLine()) != null) {
                        sb.append(responseData);
                    }
                    System.out.println(sb.toString());
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } catch (

        MalformedURLException muep) {
            System.out.println("주소가 잘못되었거나, API 사용자 정보가 일치하지 않습니다.");
            muep.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
