package hanpoom.internal_cron.crons.dashboard.fedex.vo;

import hanpoom.internal_cron.utility.calendar.CalendarManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// public class FedexExcelRow{
//     private String column;
//     private int row;
//     private FedexShipment fedexShipment;
// }
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FedexExcelRow {
    private BooleanRow isCompleted;
    private StringRow createdAt;
    private IntRow orderNo;

    // 2022-04-05 기준 새로운 플랫폼으로 인한 DB 구조가 변경되기 전 이라 Shipment No 개념이 없음.
    private IntRow shipmentNo;
    private StringRow trackingNo;
    private StringRow newTrackingNo;
    private StringRow shipmentClass;

    private StringRow orderedAt;
    private StringRow shippedAt;
    private StringRow deliveredAt;
    private FloatRow shipDuration;

    private StringRow shipServiceType;
    private StringRow shipIssueType;
    private StringRow remark;
    private StringRow detail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StringRow {
        private String column;
        private int row;
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class IntRow {
        private String column;
        private int row;
        private int value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FloatRow {
        private String column;
        private int row;
        private float value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BooleanRow {
        private String column;
        private int row;
        private boolean value;
    }

    @Override
    public String toString() {
        String jsonListStr = new StringBuilder()
                .append("{[")
                .append(String.valueOf(this.getIsCompleted().isValue()))
                .append(",")
                .append(this.getCreatedAt().getValue())
                .append(",")
                .append(this.getOrderNo().getValue())
                .append(",")
                .append(this.getShipmentNo().getValue())
                .append(",")
                .append(this.getTrackingNo().getValue())
                .append(",")

                .append(this.getNewTrackingNo().getValue())
                .append(",")
                .append(this.getShipmentClass().getValue())
                .append(",")
                .append(this.getOrderedAt().getValue())
                .append(",")
                .append(this.getShippedAt().getValue())
                .append(",")
                .append(this.getDeliveredAt().getValue())
                .append(",")

                .append(String.valueOf(CalendarManager.getDayDifference(this.getShippedAt().getValue(),
                        this.getDeliveredAt().getValue())))
                .append(",")
                .append(this.getShipServiceType().getValue())
                .append(",")
                .append(this.getShipIssueType().getValue())
                .append(",")
                .append(this.getRemark().getValue())
                .append(",")
                .append(this.getDetail().getValue())
                .append("]}")
                .toString();

        return jsonListStr;
    }
}
