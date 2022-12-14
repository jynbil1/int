package hanpoom.internal_cron.api.shipment.fedex.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import hanpoom.internal_cron.api.shipment.fedex.vo.ship.request.UnrefinedShipData;

@Repository
@Mapper
public interface ShipmentMapper {
    public List<UnrefinedShipData> getSingleShipment();
    public List<UnrefinedShipData> getMultipleShipments();
}
