package com.codenear.butterfly.admin.products.application;

import static com.codenear.butterfly.consent.domain.ConsentType.MARKETING;
import static com.codenear.butterfly.fcm.domain.FCMMessageConstant.NEW_PRODUCT;

import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductEditResponse;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.fcm.application.FCMFacade;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Keyword;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.repository.ProductRepository;
import com.codenear.butterfly.product.exception.ProductException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final FCMFacade fcmFacade;
    private final ProductRepository productRepository;

    @Transactional
    public void createProduct(ProductCreateRequest request) {
        List<Keyword> keywords = request.keywords().stream()
                .map(Keyword::new)
                .toList();

        Product product = Product.builder()
                .productName(request.productName())
                .companyName(request.companyName())
                .description(request.description())
                .originalPrice(request.originalPrice())
                .saleRate(request.saleRate())
                .category(Category.fromValue(request.category()))
                .stockQuantity(request.stockQuantity())
                .purchaseParticipantCount(request.purchaseParticipantCount())
                .maxPurchaseCount(request.maxPurchaseCount())
                .keywords(keywords)
                .build();

        productRepository.save(product);
    }

    public List<Product> loadAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findById(id);
        product.update(request);
    }

    @Transactional(readOnly = true)
    public ProductEditResponse getProductEditInfo(Long id) {
        Product product = findById(id);
        String keywordString = product.getKeywords().stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.joining(", "));

        return new ProductEditResponse(product, keywordString);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return Arrays.stream(Category.values())
                .filter(category -> !category.getValue().equals("전체"))
                .toList();
    }

    @Transactional(readOnly = true)
    public void sendNewProductNotification() {
        fcmFacade.sendTopicMessage(NEW_PRODUCT, MARKETING.getTopic());
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
