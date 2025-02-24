package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.DiscountRateRequest;
import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
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
    public ProductInventory(ProductCreateRequest createRequest,
                            String productImage,
                            String deliveryInformation,
                            List<Keyword> keywords,
                            List<DiscountRate> discountRates,
                            List<ProductDescriptionImage> descriptionImages) {
        super(createRequest, productImage, deliveryInformation, keywords, descriptionImages);
        this.originalPrice = createRequest.originalPrice();
        this.stockQuantity = createRequest.stockQuantity();
        this.purchaseParticipantCount = createRequest.purchaseParticipantCount();
        this.maxPurchaseCount = createRequest.maxPurchaseCount();
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
        return getDiscountRateForParticipationRate(participationRate);
    }

    public boolean isSoldOut() {
        return stockQuantity.equals(0);
    }

    public void decreaseQuantity(int quantity) {
        this.stockQuantity -= quantity;
    }

    public void increasePurchaseParticipantCount(int quantity) {
        // TODO : 현재 기본값 5. 매직넘버 리팩토링 필요
        int defaultIncreaseCount = 5;
        this.purchaseParticipantCount += quantity;

        if (this.purchaseParticipantCount >= this.maxPurchaseCount) {
            // maxPurchaseCount를 기본값으로 증가시키되, 최대값을 넘지 않도록 조정
            int newMaxPurchaseCount = this.maxPurchaseCount + defaultIncreaseCount;
            int maxAllowedPurchaseCount = this.purchaseParticipantCount + stockQuantity;

            this.maxPurchaseCount = Math.min(newMaxPurchaseCount, maxAllowedPurchaseCount);
        }
    }

    public void increaseQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreasePurchaseParticipantCount(int quantity) {
        // TODO : 현재 기본값 5. 매직넘버 리팩토링 필요
        int defaultDecreaseCount = 5;
        this.purchaseParticipantCount -= quantity;

        // 구매 개수가 이전 maxPurchaseCount 보다 적어졌다면, maxPurchaseCount도 줄이기
        if (this.purchaseParticipantCount < this.maxPurchaseCount - defaultDecreaseCount) {
            int newMaxPurchaseCount = this.maxPurchaseCount - defaultDecreaseCount;

            // 최소 구매 개수를 5로 설정 (이하로 내려가지 않도록)
            this.maxPurchaseCount = Math.max(defaultDecreaseCount, newMaxPurchaseCount);
        }

    }

    public Float calculateGauge() {
        return Math.round((float) purchaseParticipantCount / maxPurchaseCount * 1000f) / 1000f;
    }

    private double calculateParticipationRate() {
        return ((double) purchaseParticipantCount / (purchaseParticipantCount + stockQuantity)) * 100;
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
                .filter(rate -> participationRate >= rate.getMinParticipationRate()
                        && participationRate <= rate.getMaxParticipationRate())
                .findFirst()
                .map(DiscountRate::getDiscountRate)
                .orElse(BigDecimal.ZERO);
    }
}