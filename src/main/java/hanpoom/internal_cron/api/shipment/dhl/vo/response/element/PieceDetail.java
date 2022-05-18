package hanpoom.internal_cron.api.shipment.dhl.vo.response.element;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PieceDetail {
    private String lincensePlate;
    private int noOfPiece;

    private float actualDepth;
    private float actualWidth;
    private float actualHeight;
    private float actualWeight;

    private float depth;
    private float width;
    private float height;
    private float weight;

    private float dimWeight;
    private String weightUnit;

    public void setPieceDetail(String lincensePlate, int noOfPiece,
            float actualDepth, float actualWidth, float actualHeight, float actualWeight,
            float depth, float width, float height, float weight,
            float dimWeight, String weightUnit) {
        this.lincensePlate = lincensePlate;
        this.noOfPiece = noOfPiece;

        this.actualDepth = actualDepth;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;
        this.actualWeight = actualWeight;

        this.depth = depth;
        this.width = width;
        this.height = height;
        this.weight = weight;

        this.dimWeight = dimWeight;
        this.weightUnit = weightUnit;
    }

}
