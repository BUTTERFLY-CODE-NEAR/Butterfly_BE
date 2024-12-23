package com.codenear.butterfly.product.domain;

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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    @Column(nullable = false)
    private String productName;

    @Setter
    private String productImage;

    @Lob
    private String description;

    private String productVolume;

    private String expirationDate;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal saleRate;

    @Convert(converter = CategoryConverter.class)
    @Column(nullable = false)
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Keyword> keywords = new ArrayList<>();

    protected Product(
            String productName,
            String companyName,
            String description,
            String productImage,
            BigDecimal saleRate,
            Category category,
            List<Keyword> keywords
    ) {
        this.productName = productName;
        this.companyName = companyName;
        this.description = description;
        this.productImage = productImage;
        this.saleRate = saleRate;
        this.category = category;
        if (keywords != null) {
            this.keywords.addAll(keywords);
        }
    }

    protected void updateBasicInfo(ProductUpdateRequest request) {
        this.productName = request.getProductName();
        this.companyName = request.getCompanyName();
        this.description = request.getDescription();
        this.saleRate = request.getSaleRate();
        this.category = request.getCategory();
        this.productVolume = request.getProductVolume();
        this.expirationDate = request.getExpirationDate();
        updateKeywordsIfPresent(request.getKeywords());
    }

    protected void updateKeywordsIfPresent(List<String> newKeywordValues) {
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

    public abstract void update(ProductUpdateRequest request);
}
