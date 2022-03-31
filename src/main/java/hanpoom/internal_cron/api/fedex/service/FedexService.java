package hanpoom.internal_cron.api.fedex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.api.fedex.manager.FedexTrackManager;
import hanpoom.internal_cron.api.fedex.vo.ship.request.UnrefinedShipData;
import hanpoom.internal_cron.api.fedex.vo.ship.response.CreateShipResponse;

@Service
public class FedexService {
    @Autowired
    private FedexTrackManager fedexTrackManager;

    public List<UnrefinedShipData> getMultipleFedexLabelData(String fedexShipType) {
        return null;
    }

    public UnrefinedShipData getSingleFedexLabelData(int shipmentId, String fedexShipType) {
        return null;
    }
    //
    public CreateShipResponse createShipment() {

        // 1. 데이터 가져오기
        // 1.5. 발송일자 계산하기.
        // 2. 가져온 데이터로 운송장 발급
        // 3. 운송장 데이터 DB 갱신
        /////// 3.1. 운송장 값 갱신
        ////////// 3.1.1. 어드민 기록
        ////////// 3.1.2. WMS 기록
        /////// 3.2. 운송장 생성 여부 기록
        // 4. S3 에 업로드
        // 5. 슬랙 알림
        // 6. 인쇄
        return null;
    }
}
