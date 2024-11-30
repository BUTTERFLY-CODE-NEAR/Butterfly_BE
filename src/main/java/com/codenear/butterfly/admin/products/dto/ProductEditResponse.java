package com.codenear.butterfly.admin.products.dto;

import com.codenear.butterfly.product.domain.Product;

public record ProductEditResponse(
        Product product,
        String keywordString
) {

}