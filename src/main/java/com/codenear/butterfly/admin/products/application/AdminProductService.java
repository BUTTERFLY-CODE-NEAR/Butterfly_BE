package com.codenear.butterfly.admin.products.application;

import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductEditResponse;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Keyword;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductImage;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.FavoriteRepository;
import com.codenear.butterfly.product.domain.repository.KeywordRedisRepository;
import com.codenear.butterfly.product.domain.repository.KeywordRepository;
import com.codenear.butterfly.product.domain.repository.ProductImageRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.exception.ProductException;
import com.codenear.butterfly.s3.application.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codenear.butterfly.consent.domain.ConsentType.MARKETING;
import static com.codenear.butterfly.notify.NotifyMessage.NEW_PRODUCT;
import static com.codenear.butterfly.s3.domain.S3Directory.PRODUCT_IMAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {
    private final S3Service s3Service;
    private final FCMFacade fcmFacade;
    private final ProductInventoryRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductImageRepository productImageRepository;
    private final PaymentRedisRepository kakaoPaymentRedisRepository;
    private final KeywordRedisRepository keywordRedisRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public void createProduct(ProductCreateRequest request) {
        List<Keyword> keywords = request.keywords().stream()
                .map(Keyword::new)
                .toList();

        String deliveryInformation = request.deliveryInformation();
        if (deliveryInformation.isEmpty()) {
            deliveryInformation = "6시 이후 순차배송";
        }

        ProductInventory product = ProductInventory.builder()
                .createRequest(request)
                .deliveryInformation(deliveryInformation)
                .keywords(keywords)
                .build();
        productRepository.save(product);
        kakaoPaymentRedisRepository.saveStockQuantity(request.productName(), request.stockQuantity());

        saveImage(request.productImage(), product, ProductImage.ImageType.MAIN);
        saveImage(request.descriptionImages(), product, ProductImage.ImageType.DESCRIPTION);
        saveKeywordForRedis(keywords);
    }

    public List<ProductInventory> loadAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findById(id);

        updateImage(request.getProductImage(), product, ProductImage.ImageType.MAIN);
        updateImage(request.getDescriptionImages(), product, ProductImage.ImageType.DESCRIPTION);

        List<Keyword> existingKeywords = keywordRepository.findAllByProductId(id);
        product.update(request);

        List<Keyword> newKeywords = request.getKeywords().stream()
                .map(Keyword::new)
                .toList();

        keywordRedisRepository.updateKeywords(newKeywords, existingKeywords);
        kakaoPaymentRedisRepository.saveStockQuantity(product.getProductName(), request.getStockQuantity());
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

    @Transactional
    public void sendNewProductNotification() {
        fcmFacade.sendTopicMessage(NEW_PRODUCT, MARKETING.getTopic());
    }

    @Transactional
    public void deleteProduct(Long id) {
        ProductInventory product = findById(id);
        favoriteRepository.deleteByProduct_Id(id);
        productRepository.delete(product);
    }

    @Transactional
    public void deleteDiscountRate(Long productId, int index) {
        ProductInventory product = findById(productId);
        if (index >= 0 && index < product.getDiscountRates().size()) {
            product.getDiscountRates().remove(index);
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("유효하지 않은 할인율 인덱스입니다.");
        }
    }

    public ProductInventory findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND, null));
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String imageConverter(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileName = s3Service.uploadFile(file, PRODUCT_IMAGE);
            return s3Service.generateFileUrl(fileName, PRODUCT_IMAGE);
        }
        return null;
    }

    private void deleteS3UploadedFile(String imageUrl) {
        String existingFileName = extractFileNameFromUrl(imageUrl);
        s3Service.deleteFile(existingFileName, PRODUCT_IMAGE);
    }

    private List<ProductImage> saveImage(List<MultipartFile> images, Product product, ProductImage.ImageType imageType) {
        if (!images.get(0).isEmpty() && images.get(0) != null) {
            List<ProductImage> productImages = images.stream()
                    .map(image -> ProductImage.builder()
                            .imageType(imageType)
                            .imageUrl(imageConverter(image))
                            .product(product)
                            .build())
                    .toList();
            return productImageRepository.saveAll(productImages);
        }
        return null;
    }

    private void updateImage(List<MultipartFile> images, Product product, ProductImage.ImageType imageType) {
        if (!images.get(0).isEmpty() && images.get(0) != null) {
            List<ProductImage> descriptionImages = productImageRepository.findAllByProductIdAndImageType(product.getId(), imageType);
            descriptionImages
                    .forEach(image -> {
                        deleteS3UploadedFile(image.getImageUrl());
                        productImageRepository.deleteById(image.getId());
                    });
            List<ProductImage> newImages = saveImage(images, product, imageType);
            switch (imageType) {
                case MAIN -> product.updateMainImage(newImages);
                case DESCRIPTION -> product.updateDescriptionImage(newImages);
            }
        }
    }

    private void saveKeywordForRedis(List<Keyword> keywords) {
        if (!keywords.isEmpty()) {
            keywordRedisRepository.saveKeyword(keywords);
        }
    }
}
