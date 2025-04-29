package com.codenear.butterfly.payment.util;

import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventorySyncScheduler {
    private final ProductInventoryRepository productInventoryRepository;
    private final PaymentRedisRepository kakaoPaymentRedisRepository;

    /**
     * DB재고와 Redis재고 동기화 스케줄링 (새벽 4시)
     */
    @Scheduled(cron = "0 0 4 * * *")
    private void syncStockQuantity() {
        List<ProductInventory> products = productInventoryRepository.findAll();

        next:
        for (ProductInventory product : products) {
            if (isRemainQuantity(product)) {
                kakaoPaymentRedisRepository.saveStockQuantity(product.getProductName(), product.getStockQuantity());
                continue next;
            }
            kakaoPaymentRedisRepository.removeRemainderProduct(product.getProductName());
        }
    }

    /**
     * DB에 남은 재고가 0인지 확인한다.
     *
     * @param product 상품 이름
     * @return 재고 현황 (true / false)
     */
    private boolean isRemainQuantity(ProductInventory product) {
        return product.getStockQuantity() > 0;
    }
}
