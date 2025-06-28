package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.product.domain.SBMealType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ProductRedisRepository {
    private final String SMALL_BUSINESS_PRODUCT_IDS_KEY = "current_small_business_product_ids";
    private final RedisTemplate<String, Long> redisTemplateByNumeric;
    private final AdminProductService productService;

    /**
     * 시간대(mealType)에 맞는 상품 삽입 (갱신)
     *
     * @param mealType 판매 시간 (LUNCH,DINNER)
     */
    public void saveCurrentSmallBusinessProductIds(SBMealType mealType) {
        // TTL시간이 걸려있지만 최신화 예방 차원에서 이전 데이터 제거
        redisTemplateByNumeric.delete(SMALL_BUSINESS_PRODUCT_IDS_KEY);

        List<Long> productIds = productService.loadSmallBusinessProductsByMealType(mealType);
        if (!productIds.isEmpty()) {
            // Redis List의 오른쪽(끝)에 모든 ID를 한 번에 추가
            redisTemplateByNumeric.opsForList().rightPushAll(SMALL_BUSINESS_PRODUCT_IDS_KEY, productIds);

            // Redis List 키에 TTL 설정
            redisTemplateByNumeric.expire(SMALL_BUSINESS_PRODUCT_IDS_KEY, 1000, TimeUnit.SECONDS);
        }
    }

    /**
     * 소상공인 상품 아이디 리스트 조회
     *
     * @return 아이디 리스트
     */
    public List<Long> loadSmallBusinessProductIds() {
        return redisTemplateByNumeric.opsForList().range(SMALL_BUSINESS_PRODUCT_IDS_KEY, 0, -1);
    }
}
