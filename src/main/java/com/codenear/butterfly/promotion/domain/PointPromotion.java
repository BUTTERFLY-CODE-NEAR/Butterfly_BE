package com.codenear.butterfly.promotion.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("POINT")
public class PointPromotion extends Promotion {

    private BigDecimal rewardAmount;

    private int usedAmount; // 프로모션 지급한 금액

    private int totalAmount; // 프로모션 최대 한도 금액
}
