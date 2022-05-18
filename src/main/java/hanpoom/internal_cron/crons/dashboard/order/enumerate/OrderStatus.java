package hanpoom.internal_cron.crons.dashboard.order.enumerate;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    CHILD_STATUS(Arrays.asList("wc-completed", "wc-reg-refunded", "wc-cold-refunded", "wc-cbe-refunded"));

    private final List<String> statusList;

    OrderStatus(List<String> statusList) {
        this.statusList = statusList;
    }

    public List<String> getStatusList() {
        return this.statusList;
    }
}
