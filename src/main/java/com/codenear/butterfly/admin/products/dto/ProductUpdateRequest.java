package com.codenear.butterfly.admin.products.dto;

import com.codenear.butterfly.product.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String productName;
    private String companyName;
    private String description;
    private String productVolume;
    private String expirationDate;
    private MultipartFile productImage;
    private String existingProductImageUrl;
    private Integer originalPrice;
    private BigDecimal saleRate;
    private String category;
    private Integer quantity;
    private Integer purchaseParticipantCount;
    private Integer maxPurchaseCount;
    private Integer stockQuantity;
    private List<String> keywords;
    private List<DiscountRateRequest> discountRates;

    public Category getCategory() {
        return Category.fromValue(category);
    }
}