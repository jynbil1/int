package hanpoom.internal_cron.utility.shipment.dhl.vo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import hanpoom.internal_cron.utility.shipment.dhl.vo.response.sub.DHLArrayOfPieceEventItem;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DHLPieces {

    @Setter
    @ToString
    @Getter
    private class PieceInfo {

        @Setter
        @ToString
        @Getter
        private class ArrayOfPieceInfoItem {

            @Setter
            @ToString
            @Getter
            private class PieceDetails {

                @JsonProperty("ActualWeight")
                private float actualWeight;

                @JsonProperty("DimWeight")
                private float dimWeight;

                @JsonProperty("LicensePlate")
                private String licensePlate;

                @JsonProperty("PieceNumber")
                private float pieceNumber;

                @JsonProperty("WeightUnit")
                private String weightUnit;

                @JsonProperty("AWBNumber")
                private float aWBNumber;

                @JsonProperty("ActualDepth")
                private float actualDepth;

                @JsonProperty("Depth")
                private float depth;

                @JsonProperty("Weight")
                private float weight;

                @JsonProperty("PackageType")
                private String packageType;

                @JsonProperty("Height")
                private float height;

                @JsonProperty("Width")
                private float width;

                @JsonProperty("ActualWidth")
                private float actualWidth;

                @JsonProperty("ActualHeight")
                private float actualHeight;

            }

            @Setter
            @ToString
            @Getter
            private class PieceEvent {
                @JsonProperty("ArrayOfPieceInfoItem")
                private List<DHLArrayOfPieceEventItem> arrayOfPieceEventItem;
            }

            // Declaration
            private PieceDetails pieceDetails;
            private PieceEvent pieceEvent;
        }

        // Declaration
        private ArrayOfPieceInfoItem arrayOfPieceInfoItem;
    }

    // Declaration
    private PieceInfo PieceInfo;

}
