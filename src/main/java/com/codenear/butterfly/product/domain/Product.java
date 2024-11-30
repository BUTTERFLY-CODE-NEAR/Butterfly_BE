package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.DiscountRateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.product.util.CategoryConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    @Column(nullable = false)
    private String productName;

    private String productImage;

    @Lob
    private String description;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal saleRate;

    @Convert(converter = CategoryConverter.class)
    @Column(nullable = false)
    private Category category;

    private Integer quantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Option> options;

    //공동구매 신청현황(인원)
    @Column(nullable = false)
    private Integer purchaseParticipantCount;

    @Column(nullable = false)
    private Integer maxPurchaseCount;

    //재고수량
    @Column(nullable = false)
    private Integer stockQuantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountRate> discountRates = new ArrayList<>();

    public void update(ProductUpdateRequest request) {
        updateBasicInfo(
                request.getProductName(),
                request.getCompanyName(),
                request.getDescription(),
                request.getProductImage(),
                request.getOriginalPrice(),
                request.getSaleRate(),
                request.getCategory(),
                request.getQuantity()
        );

        updatePurchaseInfo(
                request.getPurchaseParticipantCount(),
                request.getMaxPurchaseCount(),
                request.getStockQuantity()
        );

        updateKeywordsIfPresent(request.getKeywords());
        updateDiscountRatesIfPresent(request.getDiscountRates());
    }

    private void updateBasicInfo(
            String productName,
            String companyName,
            String description,
            String productImage,
            Integer originalPrice,
            BigDecimal saleRate,
            Category category,
            Integer quantity
    ) {
        this.productName = productName;
        this.companyName = companyName;
        this.description = description;
        this.productImage = productImage;
        this.originalPrice = originalPrice;
        this.saleRate = saleRate;
        this.category = category;
        this.quantity = quantity;
    }

    private void updatePurchaseInfo(
            Integer purchaseParticipantCount,
            Integer maxPurchaseCount,
            Integer stockQuantity
    ) {
        this.purchaseParticipantCount = purchaseParticipantCount;
        this.maxPurchaseCount = maxPurchaseCount;
        this.stockQuantity = stockQuantity;
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
                (double) purchaseParticipantCount / maxPurchaseCount;
    }

    public boolean isSoldOut() {
        return stockQuantity == 0;
    }
}
