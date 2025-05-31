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
import java.util.stream.IntStream;

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
                            String deliveryInformation,
                            List<ProductImage> productImage,
                            List<Keyword> keywords,
                            List<ProductImage> descriptionImages) {
        super(createRequest, productImage, deliveryInformation, keywords, descriptionImages);
        this.originalPrice = createRequest.getOriginalPrice();
        this.stockQuantity = createRequest.getStockQuantity();
        this.purchaseParticipantCount = createRequest.getPurchaseParticipantCount();
        this.maxPurchaseCount = createRequest.getMaxPurchaseCount();
        if (createRequest.getDiscountRates() != null) {
            updateDiscountRatesIfPresent(createRequest.getDiscountRates());
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

    public void increasePurchaseParticipantCount(int quantity, int defaultMaxPurchaseNum) {
        this.purchaseParticipantCount += quantity;

        if (this.purchaseParticipantCount >= this.maxPurchaseCount) {
            // maxPurchaseCount를 기본값으로 증가시키되, 최대값을 넘지 않도록 조정
            int newMaxPurchaseCount = this.maxPurchaseCount + defaultMaxPurchaseNum;
            int maxAllowedPurchaseCount = this.purchaseParticipantCount + stockQuantity;

            this.maxPurchaseCount = Math.min(newMaxPurchaseCount, maxAllowedPurchaseCount);
        }
    }

    public void increaseQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreasePurchaseParticipantCount(int quantity, int defaultMaxPurchaseNum) {
        this.purchaseParticipantCount -= quantity;

        // 구매 개수가 이전 maxPurchaseCount 보다 적어졌다면, maxPurchaseCount도 줄이기
        if (this.purchaseParticipantCount < this.maxPurchaseCount - defaultMaxPurchaseNum) {
            int newMaxPurchaseCount = this.maxPurchaseCount - defaultMaxPurchaseNum;

            // 최소 구매 개수를 5로 설정 (이하로 내려가지 않도록)
            this.maxPurchaseCount = Math.max(defaultMaxPurchaseNum, newMaxPurchaseCount);
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

    /**
     * 다음 할인율이 있는지 확인하고 반환한다.
     *
     * @return 할인율
     */
    public BigDecimal getNextDiscountRate() {
        double participationRate = calculateParticipationRate();

        int nextDiscountRateIndex = IntStream.range(0, discountRates.size())
                .filter(i -> discountRates.get(i).getMinParticipationRate() > participationRate) // 다음 할인율 찾기
                .min()
                .orElse(-1);

        return (nextDiscountRateIndex != -1)
                ? discountRates.get(nextDiscountRateIndex).getDiscountRate()
                : BigDecimal.ZERO;
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