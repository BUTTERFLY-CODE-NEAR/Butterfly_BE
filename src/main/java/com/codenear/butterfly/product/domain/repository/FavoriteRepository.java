package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, QuerydslPredicateExecutor<Favorite> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    void deleteByProduct_Id(Long productId);

    @Query("SELECT f.product.id FROM Favorite f WHERE f.member.id = :memberId")
    List<Long> findAllProductIdByMemberId(Long memberId);
}