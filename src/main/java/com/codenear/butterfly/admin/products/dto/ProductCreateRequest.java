package com.codenear.butterfly.admin.products.dto;

import com.codenear.butterfly.product.domain.SBMealType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateRequest {
    private String productType;
    private List<MultipartFile> productImage;
    private String productName;
    private String companyName;
    private String description;
    private String productVolume;
    private String expirationDate;
    private Integer originalPrice;
    private BigDecimal saleRate;
    private String category;
    private Integer quantity;
    private Integer purchaseParticipantCount;
    private Integer maxPurchaseCount;
    private Integer stockQuantity;
    private List<String> keywords;
    private String deliveryInformation;
    private List<MultipartFile> descriptionImages;
    private List<DiscountRateRequest> discountRates = new ArrayList<DiscountRateRequest>();
    private SBMealType mealType;
}

