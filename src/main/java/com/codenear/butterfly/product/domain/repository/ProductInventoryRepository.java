package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.SBMealType;
import com.codenear.butterfly.product.domain.SmallBusinessProduct;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    List<ProductInventory> findProductByCategory(Category category);

    ProductInventory findProductByProductName(String productName);

    List<ProductInventory> findAllByProductNameIn(Set<String> productNames);

    @Query(value = "select P.* from product P where P.product_type = :productType", nativeQuery = true)
    List<ProductInventory> findByProductType(@Param(value = "productType") String productType);

    @Query(value = "SELECT SB.id from SmallBusinessProduct SB where SB.mealType = :mealType")
    List<Long> findIdsByMealType(@Param(value = "mealType") SBMealType mealType);

    @EntityGraph(attributePaths = {"discountRates"})
    List<SmallBusinessProduct> findByIdIn(List<Long> productIds);
}
