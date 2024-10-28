package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findProductByCategory(Category category);

    Product findProductByProductName(String productName);
}
