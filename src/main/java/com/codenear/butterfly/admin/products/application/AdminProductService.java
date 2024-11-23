package com.codenear.butterfly.admin.products.application;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;

    public List<Product> loadAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product updateProduct(Long id, Product updateRequest) {
        Product product = findById(id);

        product.updateProductInfo(
                updateRequest.getProductName(),
                updateRequest.getCompanyName(),
                updateRequest.getDescription(),
                updateRequest.getProductImage(),
                updateRequest.getOriginalPrice(),
                updateRequest.getSaleRate(),
                updateRequest.getCategory(),
                updateRequest.getQuantity()
        );

        product.updatePurchaseInfo(
                updateRequest.getPurchaseParticipantCount(),
                updateRequest.getMaxPurchaseCount(),
                updateRequest.getStockQuantity()
        );

        product.updateOptions(updateRequest.getOptions());
        product.updateKeywords(updateRequest.getKeywords());
        product.updateDiscountRates(updateRequest.getDiscountRates());

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return Arrays.stream(Category.values())
                .filter(category -> !category.getValue().equals("전체"))
                .toList();
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND, null));
    }
}
