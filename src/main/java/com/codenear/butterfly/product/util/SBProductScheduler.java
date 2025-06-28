package com.codenear.butterfly.product.util;

import com.codenear.butterfly.product.domain.SBMealType;
import com.codenear.butterfly.product.domain.repository.ProductRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SBProductScheduler {
    private final ProductRedisRepository productRedisRepository;

    // 매일 자정 (00:00)에 '점심' 상품을 'current_small_business_products' Redis Hash Key에 캐싱
    @Scheduled(cron = "*/8 * * * * *")
    public void updateToLunchProducts() {
        log.info("공통 캐시에 점심 상품 업데이트 스케줄러 실행: {}", LocalTime.now());
        productRedisRepository.saveCurrentSmallBusinessProductIds(SBMealType.LUNCH);
    }

    // 매일 12시 30분 (12:30)에 '저녁' 상품을 Redis Hash Key에 캐싱
    @Scheduled(cron = "*/13 * * * * *")
    public void updateToDinnerProducts() {
        log.info("공통 캐시에 저녁 상품 업데이트 스케줄러 실행: {}", LocalTime.now());
        productRedisRepository.saveCurrentSmallBusinessProductIds(SBMealType.DINNER);
    }
}
