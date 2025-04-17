package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.product.util.CategoryConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

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

    @Lob
    private String description;

    private String productVolume;

    private String expirationDate;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal saleRate;

    @Convert(converter = CategoryConverter.class)
    @Column(nullable = false)
    private Category category;

    @Column
    private String deliveryInformation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<Option> options = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "image_type = 'DESCRIPTION'")
    private List<ProductImage> descriptionImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "image_type = 'MAIN'")
    private List<ProductImage> productImage = new ArrayList<>();

    protected Product(ProductCreateRequest createRequest,
                      List<ProductImage> productImage,
                      String deliveryInformation,
                      List<Keyword> keywords,
                      List<ProductImage> descriptionImages) {

        this.productName = createRequest.productName();
        this.companyName = createRequest.companyName();
        this.description = createRequest.description();
        this.productImage = productImage;
        this.saleRate = createRequest.saleRate();
        this.category = Category.fromValue(createRequest.category());
        if (keywords != null) {
            this.keywords.addAll(keywords);
        }
        this.deliveryInformation = deliveryInformation;
        this.descriptionImages = descriptionImages;
    }

    public void updateDescriptionImage(List<ProductImage> newDescriptionImages) {
        this.descriptionImages = newDescriptionImages;
    }

    public void updateMainImage(List<ProductImage> newMainImages) {
        this.productImage = newMainImages;
    }

    protected void updateBasicInfo(ProductUpdateRequest request) {
        this.productName = request.getProductName();
        this.companyName = request.getCompanyName();
        this.description = request.getDescription();
        this.saleRate = request.getSaleRate();
        this.category = request.getCategory();
        this.productVolume = request.getProductVolume();
        this.expirationDate = request.getExpirationDate();
        this.deliveryInformation = request.getDeliveryInformation();
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
