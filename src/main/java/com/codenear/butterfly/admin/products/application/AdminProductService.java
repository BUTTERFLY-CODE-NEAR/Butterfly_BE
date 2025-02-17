package com.codenear.butterfly.admin.products.application;

import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductEditResponse;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.kakaoPay.domain.repository.KakaoPaymentRedisRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Keyword;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductDescriptionImage;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.FavoriteRepository;
import com.codenear.butterfly.product.domain.repository.ProductDescriptionImageRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.exception.ProductException;
import com.codenear.butterfly.s3.application.S3Service;
import lombok.RequiredArgsConstructor;
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
public class AdminProductService {
    private final S3Service s3Service;
    private final FCMFacade fcmFacade;
    private final ProductInventoryRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductDescriptionImageRepository productDescriptionImageRepository;
    private final KakaoPaymentRedisRepository kakaoPaymentRedisRepository;

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
                .productImage(imageConverter(request.productImage()))
                .deliveryInformation(deliveryInformation)
                .keywords(keywords)
                .build();
        productRepository.save(product);
        kakaoPaymentRedisRepository.saveStockQuantity(request.productName(), request.stockQuantity());
        if (request.descriptionImages() != null && !request.descriptionImages().isEmpty()) {
            List<ProductDescriptionImage> descriptionImages = getDescriptionImages(request.descriptionImages(), product);
            productDescriptionImageRepository.saveAll(descriptionImages);
        }
    }

    public List<ProductInventory> loadAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findById(id);

        if (request.getProductImage() != null && !request.getProductImage().isEmpty()) {
            if (product.getProductImage() != null) {
                String existingFileName = extractFileNameFromUrl(product.getProductImage());
                s3Service.deleteFile(existingFileName, PRODUCT_IMAGE);
            }

            String fileName = s3Service.uploadFile(request.getProductImage(), PRODUCT_IMAGE);
            String imageUrl = s3Service.generateFileUrl(fileName, PRODUCT_IMAGE);
            product.setProductImage(imageUrl);
        }

        // 상품 설명 이미지 업데이트
        if (request.getDescriptionImages() != null && !request.getDescriptionImages().isEmpty()) {
            List<ProductDescriptionImage> descriptionImages = productDescriptionImageRepository.findAllByProductId(product.getId());
            descriptionImages
                    .forEach(image -> {
                        String fileName = extractFileNameFromUrl(image.getImageUrl());
                        s3Service.deleteFile(fileName, PRODUCT_IMAGE);
                        productDescriptionImageRepository.deleteById(image.getId());
                    });
            List<ProductDescriptionImage> newDescriptionImages = getDescriptionImages(request.getDescriptionImages(), product);
            productDescriptionImageRepository.saveAll(newDescriptionImages);
            product.updateDescriptionImage(newDescriptionImages);
        }

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

    private List<ProductDescriptionImage> getDescriptionImages(List<MultipartFile> images, Product product) {
        return images.stream()
                .map(image -> ProductDescriptionImage.builder()
                        .imgUrl(imageConverter(image))
                        .product(product)
                        .build())
                .toList();
    }

}
