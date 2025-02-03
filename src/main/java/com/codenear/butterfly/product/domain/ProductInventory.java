package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.DiscountRateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INVENTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInventory extends Product {

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer purchaseParticipantCount;

    @Column(nullable = false)
    private Integer maxPurchaseCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountRate> discountRates = new ArrayList<>();

    @Builder
    public ProductInventory(
            String productName,
            String companyName,
            String description,
            String productImage,
            Integer originalPrice,
            BigDecimal saleRate,
            Category category,
            Integer stockQuantity,
            Integer purchaseParticipantCount,
            Integer maxPurchaseCount,
            List<Keyword> keywords,
            List<DiscountRate> discountRates
    ) {
        super(productName, companyName, description, productImage, saleRate, category, keywords);
        this.originalPrice = originalPrice;
        this.stockQuantity = stockQuantity;
        this.purchaseParticipantCount = purchaseParticipantCount;
        this.maxPurchaseCount = maxPurchaseCount;
        if (discountRates != null) {
            this.discountRates.addAll(discountRates);
        }
    }

    @Override
    public void update(ProductUpdateRequest request) {
        super.updateBasicInfo(request);
        this.originalPrice = request.getOriginalPrice();
        this.stockQuantity = request.getStockQuantity();
        this.purchaseParticipantCount = request.getPurchaseParticipantCount();
        this.maxPurchaseCount = request.getMaxPurchaseCount();
        updateDiscountRatesIfPresent(request.getDiscountRates());
    }

    private void updateDiscountRatesIfPresent(List<DiscountRateRequest> newRates) {
        if (newRates == null) {
            return;
        }

        discountRates.clear();
        List<DiscountRate> rates = newRates.stream()
                .map(request -> new DiscountRate(
                        this,
                        request.getMinParticipationRate(),
                        request.getMaxParticipationRate(),
                        request.getDiscountRate()
                ))
                .toList();

        discountRates.addAll(rates);
    }

    public BigDecimal getCurrentDiscountRate() {
        double participationRate = calculateParticipationRate();
        return discountRates.stream()
                .filter(rate -> participationRate > rate.getMinParticipationRate()
                        && participationRate <= rate.getMaxParticipationRate())
                .findFirst()
                .map(DiscountRate::getDiscountRate)
                .orElse(BigDecimal.ZERO);
    }

    public boolean isSoldOut() {
        return stockQuantity.equals(0);
    }

    public void decreaseQuantity(int quantity) {
        this.stockQuantity -= quantity;
    }

    public void increasePurchaseParticipantCount(int quantity) {
        this.purchaseParticipantCount += quantity;
        if (this.purchaseParticipantCount >= this.maxPurchaseCount) {
            this.purchaseParticipantCount %= this.maxPurchaseCount;
        }
    }

    public Float calculateGauge() {
        return Math.round((float) purchaseParticipantCount / maxPurchaseCount * 1000f) / 1000f;
    }

    private double calculateParticipationRate() {
        return maxPurchaseCount == 0 ? 0 :
                ((double) purchaseParticipantCount / maxPurchaseCount) * 100;
    }

    public int calculatePointRefund(int quantity) {
        int currentParticipantCount = (this.purchaseParticipantCount + quantity) % this.maxPurchaseCount;
        double participationRate = ((double) currentParticipantCount / this.maxPurchaseCount) * 100;
        BigDecimal nextDiscountRate = getDiscountRateForParticipationRate(participationRate);

        int sectionCount = calculateSectionCount();
        int discountQuantity = currentParticipantCount % sectionCount;
        int totalAmount = this.originalPrice * discountQuantity;
        int nextDiscountAmount = totalAmount * nextDiscountRate.intValue() / 100;
        int currentDiscountAmount = totalAmount * getCurrentDiscountRate().intValue() / 100;
        int pointRefundAmount = nextDiscountAmount - currentDiscountAmount;

        return Math.max(pointRefundAmount, 0);
    }

    private int calculateSectionCount() {
        return discountRates.stream()
                .findFirst()
                .map(rate -> (int) (this.maxPurchaseCount * (rate.getMaxParticipationRate() / 100)))
                .orElse(1);
    }

    private BigDecimal getDiscountRateForParticipationRate(double participationRate) {
        return discountRates.stream()
                .filter(rate -> participationRate > rate.getMinParticipationRate()
                        && participationRate <= rate.getMaxParticipationRate())
                .findFirst()
                .map(DiscountRate::getDiscountRate)
                .orElse(BigDecimal.ZERO);
    }
}