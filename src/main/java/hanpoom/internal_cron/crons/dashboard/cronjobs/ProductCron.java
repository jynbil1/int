package hanpoom.internal_cron.crons.dashboard.cronjobs;

import hanpoom.internal_cron.crons.dashboard.slack.service.ExpirationService;
import hanpoom.internal_cron.crons.dashboard.slack.service.ProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProductCron {

    private ProductService productService;

    public ProductCron(ProductService productService) {
        this.productService = productService;
    }

    // 매일 16시 30분 부정확한 가격 알림
    @Scheduled(cron = "0 30 16 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedDelay=10000, zone = "Asia/Seoul")    //(10초 단위)
    public void priceIncorrectProductCron() {
        productService.reportPriceIncorrectProduct();
    }

}
