package hanpoom.internal_cron.utility.shipment.dhl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.utility.shipment.dhl.config.MyDHLClient;
import hanpoom.internal_cron.utility.shipment.dhl.field.DHLAPIType;

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

    @Override
    public JSONArray callAPI(String requestJson) {
        System.out.println(client.toString());
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

                    if (jsonObject.optJSONArray("ArrayOfAWBInfoItem") != null) {
                        return jsonObject.getJSONArray("ArrayOfAWBInfoItem");
                    } else {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObject.getJSONObject("ArrayOfAWBInfoItem"));
                        return jsonArray;
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
        } catch (MalformedURLException muep) {
            System.out.println("주소가 잘못되었거나, API 사용자 정보가 일치하지 않습니다.");
            muep.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // private DHLTrackingResponseVO refineShipmentResponse(){}
}
