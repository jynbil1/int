package hanpoom.internal_cron.crons.dashboard.fedex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hanpoom.internal_cron.crons.dashboard.fedex.mapper.FedexMapper;
import hanpoom.internal_cron.crons.dashboard.fedex.vo.OrderShipment;

@Service
public class FedexService {

    @Autowired
    private FedexMapper fedexMapper;

    public List<OrderShipment> getShippedFedexOrders(){
        try {
            return fedexMapper.getOrderShipments();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void insertDeliveredShipments() {
        try {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertErrorShipments() {
        try {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
