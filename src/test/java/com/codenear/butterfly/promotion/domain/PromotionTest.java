package com.codenear.butterfly.promotion.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PromotionTest {

    @Test
    void 프로모션_기간이_아닌_경우_FALSE를_반환한다() {
        // given
        Promotion promotion = createPromotion(LocalDate.MAX, true);

        // when
        boolean result = promotion.isApplicable();

        // then
        assertThat(result)
                .isFalse();
    }

    @Test
    void 프로모션_기간인_경우_TRUE를_반환한다() {
        // given
        Promotion promotion = createPromotion(LocalDate.MIN, true);

        // when
        boolean result = promotion.isApplicable();

        // then
        assertThat(result)
                .isTrue();
    }

    @Test
    void 프로모션_비활성화_경우_FALSE를_반환한다() {
        // given
        Promotion promotion = createPromotion(LocalDate.MIN, false);

        // when
        boolean result = promotion.isApplicable();

        // then
        assertThat(result)
                .isFalse();
    }

    private Promotion createPromotion(LocalDate startDate, boolean active) {
        return Promotion.builder()
                .startDate(startDate)
                .endDate(LocalDate.MAX)
                .active(active)
                .build();
    }
}