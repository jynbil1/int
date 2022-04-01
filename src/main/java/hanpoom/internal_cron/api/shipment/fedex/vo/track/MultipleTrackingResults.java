package hanpoom.internal_cron.api.shipment.fedex.vo.track;

import java.util.List;

import lombok.Data;

@Data
public class MultipleTrackingResults {
    private String transactionId;
    private Output output;

    @Data
    public static class Output {
        private List<CompleteTrackResult> completeTrackResults;

        @Data
        public static class CompleteTrackResult {
            private String trackingNumber;
            private FedexTrackResponse trackResults;
            
        }
    }

}
