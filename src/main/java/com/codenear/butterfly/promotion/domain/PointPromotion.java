package com.codenear.butterfly.promotion.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("POINT")
public class PointPromotion extends Promotion {

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal rewardAmount;

    @NotNull
    private int usedAmount; // 프로모션 지급한 금액

    @NotNull
    private int totalAmount; // 프로모션 최대 한도 금액

    @Override
    public boolean isApplicable() {
        int remainedAmount = totalAmount - usedAmount;
        return remainedAmount >= rewardAmount.intValue();
    }
}
