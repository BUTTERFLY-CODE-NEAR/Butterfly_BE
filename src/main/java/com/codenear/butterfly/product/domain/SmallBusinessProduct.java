package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@DiscriminatorValue("SMALL_BUSINESS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmallBusinessProduct extends ProductInventory {
    @Enumerated(EnumType.STRING)
    private SBMealType mealType;

    @Builder(builderMethodName = "createSBBuilder", buildMethodName = "buildCreateSB")
    public SmallBusinessProduct(ProductCreateRequest request,
                                String deliveryInformation,
                                List<ProductImage> productImage,
                                List<Keyword> keywords,
                                List<ProductImage> descriptionImages) {
        super(request, deliveryInformation, productImage, keywords, descriptionImages);
        this.mealType = request.getMealType();
    }

    @Override
    public void update(ProductUpdateRequest request) {
        super.update(request);
        this.mealType = request.getMealType();
    }
}