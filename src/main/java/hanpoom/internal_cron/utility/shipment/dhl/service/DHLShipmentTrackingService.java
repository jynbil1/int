package hanpoom.internal_cron.utility.shipment.dhl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
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
    // private static final String
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

    // public DHLTrackingResponseStorage trackShipment(String trackingNo) {
    // return null;
    // }

    // public DHLTrackingResponseStorage trackShipments(String[] trackingNos) {
    // return null;
    // }

    // Recommended No => 50
    // Upto 70 has taken around 10 secs
    public DHLTrackingResponse trackShipment(String trackingNo) {
        DHLTrackingRequest request = new DHLTrackingRequest(trackingNo);
        String requestJson = request.getValidatedJSONRequest().toString();
        DHLTrackingResponse response = deserializeJsonToPojo(
                callAPI(requestJson).getJSONObject("ArrayOfAWBInfoItem"));
        return response;
    }

    public DHLTrackingResponseStorage trackShipments(List<String> trackingNos) {
        DHLTrackingRequest request = new DHLTrackingRequest(trackingNos);
        String requestJson = request.getValidatedJSONRequest().toString();
        JSONArray responseJsonArray = callAPI(requestJson).getJSONArray("ArrayOfAWBInfoItem");

        List<DHLTrackingResponse> responses = new ArrayList<>();

        for (int index = 0; index < responseJsonArray.length(); ++index) {
            responses.add(deserializeJsonToPojo(responseJsonArray.getJSONObject(index)));
        }
        return new DHLTrackingResponseStorage(responses);
    }

    private DHLTrackingResponse deserializeJsonToPojo(JSONObject jsonObject) {
        // System.out.println(jsonObject.toString());
        DHLTrackingResponse response = new DHLTrackingResponse();

        Status status = new Status();
        JSONObject responseStatus = jsonObject.getJSONObject("Status");
        if (!responseStatus.optString("Condition").isBlank()) {
            JSONObject responseCondition = responseStatus.getJSONObject("Condition")
                    .optJSONObject("ArrayOfConditionItem");
            status.setStatus(responseStatus.getString("ActionStatus"),
                    String
                            .valueOf(responseCondition.optString("ConditionCode")),
                    String
                            .valueOf(responseCondition.optString("ConditionData")));

            if (status.getConditionCode().equals("101")) {
                response.setStatus(status);
                return response;
            }
        }
        ;

        // AWBNumber
        response.setTrackingNo(String.valueOf(jsonObject.getInt("AWBNumber")));

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

        // Shipment Info -> Shipment Detail
        shipmentDetail.setShipmentDetail(shipmentInfo.getInt("Pieces"),
                shipmentInfo.optFloat("Weight"),
                shipmentInfo.getString("WeightUnit"),
                shipmentInfo.optString("ServiceType"),
                shipmentInfo.optString("ShipmentDescription"),
                shipmentInfo.optJSONObject("ShipperReference").optInt("ReferenceID"));

        // Shipment Info -> Shipment Detail -> Shipper
        JSONObject shipperObj = shipmentInfo.getJSONObject("Shipper");
        shipper.setShipperDetail(shipmentInfo.getString("ShipperName"),
                shipperObj.getString("City"), shipperObj.getString("Suburb"),
                shipperObj.getString("StateOrProvinceCode"),
                shipperObj.get("PostalCode").toString(), shipperObj.getString("CountryCode"));

        // Shipment Info -> Shipment Detail -> Consignee
        JSONObject consigneeObj = shipmentInfo.getJSONObject("Consignee");
        consignee.setConsigneeDetail(shipmentInfo.getString("ConsigneeName"),
                consigneeObj.getString("City"),
                consigneeObj.optString("Suburb"),
                consigneeObj.getString("StateOrProvinceCode"),
                consigneeObj.get("PostalCode").toString(), consigneeObj.getString("CountryCode"));

        // Shipment Info -> Shipment Events
        JSONArray shipmentEventsObj = shipmentInfo.getJSONObject("ShipmentEvent")
                .getJSONArray("ArrayOfShipmentEventItem");

        ShipmentEvent shipmentEvent = new ShipmentEvent();
        JSONObject indexedJson = new JSONObject();
        for (int index = 0; index < shipmentEventsObj.length(); ++index) {
            indexedJson = shipmentEventsObj.getJSONObject(index);
            if (index == shipmentEventsObj.length() - 1) {
                shipmentDetail.setSignatory(indexedJson.optString("Signatory"));
            }
            shipmentEvent.setShipmentEventDetail(
                    indexedJson.getString("Date"), indexedJson.getString("Time"),
                    indexedJson.getJSONObject("ServiceEvent").getString("EventCode"),
                    indexedJson.getJSONObject("ServiceEvent").getString("Description"),
                    indexedJson.getJSONObject("ServiceArea").getString("ServiceAreaCode"),
                    indexedJson.getJSONObject("ServiceArea").getString("Description"));

            shipmentEvents.add(shipmentEvent);
        }

        JSONObject pieceDetails = jsonObject.getJSONObject("Pieces").getJSONObject("PieceInfo")
                .getJSONObject("ArrayOfPieceInfoItem").getJSONObject("PieceDetails");
        // PieceDetails

        pieceDetail.setPieceDetail(
                pieceDetails.getString("LicensePlate"),
                pieceDetails.getInt("PieceNumber"),

                pieceDetails.getFloat("ActualDepth"),
                pieceDetails.getFloat("ActualWidth"),
                pieceDetails.getFloat("ActualHeight"),
                pieceDetails.getFloat("ActualWeight"),

                pieceDetails.optFloat("Depth"),
                pieceDetails.optFloat("Width"),
                pieceDetails.optFloat("Height"),
                pieceDetails.optFloat("Weight"),

                pieceDetails.getFloat("DimWeight"),
                pieceDetails.getString("WeightUnit"));

        response.setConsignee(consignee);
        response.setShipper(shipper);
        response.setPieceDetail(pieceDetail);
        response.setServiceArea(serviceArea);
        response.setShipmentDetail(shipmentDetail);
        response.setShipmentEvents(shipmentEvents);
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
