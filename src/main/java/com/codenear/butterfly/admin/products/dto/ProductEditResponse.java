package com.codenear.butterfly.admin.products.dto;

import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.SBMealType;
import com.codenear.butterfly.product.domain.SmallBusinessProduct;

public record ProductEditResponse(
        Product product,
        String keywordString,
        String productType,
        SBMealType mealType
) {
    public static ProductEditResponse from(Product product, String keywordString) {
        String type = "INVENTORY"; // 기본값
        SBMealType mealType = null;

        if (product instanceof SmallBusinessProduct smallBusinessProduct) {
            type = "SMALL_BUSINESS";
            mealType = smallBusinessProduct.getOrderType();
        }
        return new ProductEditResponse(product, keywordString, type, mealType);
    }
}