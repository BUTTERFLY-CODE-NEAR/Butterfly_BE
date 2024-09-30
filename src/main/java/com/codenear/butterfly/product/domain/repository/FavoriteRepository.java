package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, QuerydslPredicateExecutor<Favorite> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}