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
    public void priceIncorrectProductCron() {
        productService.reportPriceIncorrectProduct();
    }

    // 매일 오후 12시 00분 정가 없는 상품 알림
    @Scheduled(cron = "0 00 12 * * *", zone = "Asia/Seoul")
    public void priceRegularNoneProductCron() {
        productService.reportRegularPriceNoneProduct();
    }

}
