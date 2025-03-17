package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProductIdAndImageType(Long productId, ProductImage.ImageType imageType);
}
