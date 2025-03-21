package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findKeywordByKeyword(String keyword);

    @Query(value = "SELECT product_id FROM keyword WHERE keyword = :keyword", nativeQuery = true)
    List<Long> findAllProductIdByKeyword(@Param("keyword") String keyword);
}
