package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.ProductDescriptionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDescriptionImageRepository extends JpaRepository<ProductDescriptionImage, Long> {
    List<ProductDescriptionImage> findAllByProductId(Long productId);
}
