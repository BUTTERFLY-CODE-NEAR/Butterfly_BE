package com.codenear.butterfly.promotion.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PointPromotionTest {

    @ParameterizedTest(name = "지급 포인트: {0}, 현재 지급 상황: {1}, 최대 지급 포인트: {2}, 예상 결과: {3}")
    @CsvSource({
        "100, 200, 500, true",
        "100, 400, 500, true",
        "100, 450, 500, false",
        "100, 500, 500, false"
    })
    void 포인트_프로모션의_가능_여부를_테스트한다(BigDecimal reward, int used, int total, boolean expected) {
        // given
        PointPromotion pointPromotion = createPointPromotion(reward, used, total);

        // when
        boolean result = pointPromotion.isApplicable();

        // then
        assertThat(result)
                .isEqualTo(expected);
    }

    private PointPromotion createPointPromotion(BigDecimal reward, int used, int total) {
        return PointPromotion.builder()
                .self()
                .rewardAmount(reward)
                .usedAmount(used)
                .totalAmount(total)
                .build();
    }

}