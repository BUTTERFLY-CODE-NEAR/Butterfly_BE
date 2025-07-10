package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.DiscountRateRequest;
import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.product.domain.dto.DiscountRateDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        this.maxPurchaseCount = getInitialMaxPurchaseCount(createRequest.getDiscountRates());
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
        updateMaxPurchaseCountBasedOnCurrentParticipation();
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
        DiscountRate discountRate = getDiscountRateForParticipationRate(participationRate);
        return discountRate != null ? discountRate.getDiscountRate() : BigDecimal.ZERO;
    }

    public boolean isSoldOut() {
        return stockQuantity.equals(0);
    }

    public void decreaseQuantity(int quantity) {
        this.stockQuantity -= quantity;
    }

    public void increasePurchaseParticipantCount(int quantity) {
        this.purchaseParticipantCount += quantity;

        updateMaxPurchaseCountBasedOnCurrentParticipation();
    }

    public void increaseQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreasePurchaseParticipantCount(int quantity) {
        this.purchaseParticipantCount -= quantity;

        updateMaxPurchaseCountBasedOnCurrentParticipation();
    }

    public Float calculateGauge() {
        return Math.round((float) purchaseParticipantCount / maxPurchaseCount * 1000f) / 1000f;
    }

    /**
     * 전체 구간 할인율에 대한 최소 신청인원, 최대 신청인원, 할인율을 반환한다.
     *
     * @return
     */
    public List<DiscountRateDTO> getDiscountRateInfo() {
        final int totalPossibleParticipants = this.purchaseParticipantCount + this.stockQuantity;

        return this.discountRates.stream()
                .map(rate -> {
                    int minApplyCount = (int) Math.floor(totalPossibleParticipants * (rate.getMinParticipationRate() / 100.0));
                    int maxApplyCount = (int) Math.floor(totalPossibleParticipants * (rate.getMaxParticipationRate() / 100.0));

                    // 0% 시작 구간의 minApplyCount가 0이 아닌 경우 0으로 보정
                    if (rate.getMinParticipationRate() == 0.0) {
                        minApplyCount = 0;
                    }
                    // 100% 종료 구간의 maxApplyCount가 totalPossibleParticipants보다 크면 보정 (정확히 총 수량과 일치하도록)
                    if (rate.getMaxParticipationRate() == 100.0) {
                        maxApplyCount = totalPossibleParticipants;
                    }

                    return new DiscountRateDTO(
                            minApplyCount,
                            maxApplyCount,
                            rate.getDiscountRate().add(this.getSaleRate())
                    );
                })
                .collect(Collectors.toList());
    }

    private double calculateParticipationRate() {
        return ((double) purchaseParticipantCount / (purchaseParticipantCount + stockQuantity)) * 100;
    }

    /**
     * 현재 할인율 구간을 찾는다.
     *
     * @param participationRate 현재 구간 추가 할인율
     * @return
     */
    private DiscountRate getDiscountRateForParticipationRate(double participationRate) {
        return discountRates.stream()
                .filter(rate -> participationRate >= rate.getMinParticipationRate()
                        && participationRate <= rate.getMaxParticipationRate())
                .findFirst()
                .orElse(null);
    }

    /**
     * 초기 maxPurchaseCount의 값을 설정한다.
     * 가장 낮은 할인율 구간 (ex. 0~20)에서 최대 maxParticipationRate에 대한 최대 신청 인원을 구한다.
     *
     * @param discountRatesRequests 할인율 구간 리스트
     * @return 최대 신청 인원수
     */
    private Integer getInitialMaxPurchaseCount(List<DiscountRateRequest> discountRatesRequests) {
        return discountRatesRequests.stream()
                .min(Comparator.comparingDouble(DiscountRateRequest::getMinParticipationRate))
                .map(rate -> (int) Math.floor((this.stockQuantity + this.purchaseParticipantCount) * (rate.getMaxParticipationRate() / 100.0)))
                .orElse(1);
    }

    /**
     * 현재 참여 인원수에 대한 할인 구간을 찾고, 최대 할인율에 대한 신청 인원수를 계산하여 maxPurChaseCount에 넣는다.
     */
    private void updateMaxPurchaseCountBasedOnCurrentParticipation() {
        double currentParticipationRate = calculateParticipationRate();
        int totalPossibleParticipants = this.purchaseParticipantCount + this.stockQuantity;
        // 현재 참여율 구간을 찾는다.
        DiscountRate relevantRateSegment = getDiscountRateForParticipationRate(currentParticipationRate);
        // 유효한 할인 구간을 찾았다면, 해당 구간의 최대 참여율을 기준으로 maxPurchaseCount를 설정하고, 최대 구매수량을 넘었다면 최대구매수량으로 설정
        if (relevantRateSegment != null) {
            double maxParticipationRate = relevantRateSegment.getMaxParticipationRate();
            this.maxPurchaseCount = (int) Math.floor(totalPossibleParticipants * (maxParticipationRate / 100.0));
        } else {
            this.maxPurchaseCount = totalPossibleParticipants;
        }

    }
}