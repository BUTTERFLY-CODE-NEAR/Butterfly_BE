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
        updateBasicProductInfo(request);
        updatePurchaseDetails(request);
        updateProductKeywords(request);
        updateProductDiscountRates(request);
    }

    private void updateBasicProductInfo(ProductUpdateRequest request) {
        this.productName = request.getProductName();
        this.companyName = request.getCompanyName();
        this.description = request.getDescription();
        this.productImage = request.getProductImage();
        this.originalPrice = request.getOriginalPrice();
        this.saleRate = request.getSaleRate();
        this.category = request.getCategory();
        this.stockQuantity = request.getQuantity();
    }

    private void updatePurchaseDetails(ProductUpdateRequest request) {
        this.purchaseParticipantCount = request.getPurchaseParticipantCount();
        this.maxPurchaseCount = request.getMaxPurchaseCount();
        this.stockQuantity = request.getStockQuantity();
    }

    private void updateProductKeywords(ProductUpdateRequest request) {
        List<String> newKeywordValues = request.getKeywords();

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

    private void updateProductDiscountRates(ProductUpdateRequest request) {
        List<DiscountRateRequest> newRates = request.getDiscountRates();

        if (newRates == null) {
            return;
        }

        discountRates.clear();
        List<DiscountRate> rates = newRates.stream()
                .map(rateRequest -> new DiscountRate(
                        this,
                        rateRequest.getMinParticipationRate(),
                        rateRequest.getMaxParticipationRate(),
                        rateRequest.getDiscountRate()
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
        return stockQuantity.equals(0);
    }

    public void decreaseQuantity(int stockQuantity) {
        this.stockQuantity -= stockQuantity;
    }

    public void increasePurchaseParticipantCount() {
        this.purchaseParticipantCount += 1;
        if (this.purchaseParticipantCount.equals(this.maxPurchaseCount)) {
            this.purchaseParticipantCount = 0;
        }
    }
}
