package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    List<ProductInventory> findProductByCategory(Category category);

    ProductInventory findProductByProductName(String productName);

    List<ProductInventory> findAllByProductNameIn(Set<String> productNames);
}
