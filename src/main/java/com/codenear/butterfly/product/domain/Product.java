package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.DiscountRateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.product.util.CategoryConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Product extends ProductInventory {

    private String companyName;

    @Column(nullable = false)
    private String productName;

    @Setter
    private String productImage;

    @Lob
    private String description;

    private String productVolume;

    private String expirationDate;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal saleRate;

    @Convert(converter = CategoryConverter.class)
    @Column(nullable = false)
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Option> options;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountRate> discountRates = new ArrayList<>();

    public Product(
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
        this.productName = productName;
        this.companyName = companyName;
        this.description = description;
        this.productImage = productImage;
        this.originalPrice = originalPrice;
        this.saleRate = saleRate;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.purchaseParticipantCount = purchaseParticipantCount;
        this.maxPurchaseCount = maxPurchaseCount;

        this.keywords = keywords != null
                ? new ArrayList<>(keywords)
                : new ArrayList<>();

        this.discountRates = discountRates != null
                ? new ArrayList<>(discountRates)
                : new ArrayList<>();
    }

    public void update(ProductUpdateRequest request) {
        this.productName = request.getProductName();
        this.companyName = request.getCompanyName();
        this.description = request.getDescription();
        this.originalPrice = request.getOriginalPrice();
        this.saleRate = request.getSaleRate();
        this.category = request.getCategory();
        this.stockQuantity = request.getStockQuantity();
        this.purchaseParticipantCount = request.getPurchaseParticipantCount();
        this.maxPurchaseCount = request.getMaxPurchaseCount();
        this.productVolume = request.getProductVolume();
        this.expirationDate = request.getExpirationDate();

        updateKeywordsIfPresent(request.getKeywords());
        updateDiscountRatesIfPresent(request.getDiscountRates());
    }

    private void updateKeywordsIfPresent(List<String> newKeywordValues) {
        if (newKeywordValues == null || newKeywordValues.isEmpty()) {
            return;
        }

        Set<String> newKeywordSet = new HashSet<>(newKeywordValues);

        keywords.removeIf(keyword -> !newKeywordSet.contains(keyword.getKeyword()));

        Set<String> existingKeywordValues = keywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.toSet());

        List<Keyword> keywordsToAdd = newKeywordSet.stream()
                .filter(value -> !existingKeywordValues.contains(value))
                .map(Keyword::new)
                .toList();

        keywords.addAll(keywordsToAdd);
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

    private double calculateParticipationRate() {
        return maxPurchaseCount == 0 ? 0 :
                ((double) purchaseParticipantCount / maxPurchaseCount) * 100;
    }

    public boolean isSoldOut() {
        return stockQuantity.equals(0);
    }

    public void decreaseQuantity(int stockQuantity) {
        this.stockQuantity -= stockQuantity;
    }

    public void increasePurchaseParticipantCount(int quantity) {
        this.purchaseParticipantCount += quantity;
        if (this.purchaseParticipantCount > this.maxPurchaseCount) {
            this.purchaseParticipantCount %= this.maxPurchaseCount;
        }
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
                .orElse(0);
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
