package com.codenear.butterfly.promotion.domain.repository;

import com.codenear.butterfly.promotion.domain.PointPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointPromotionRepository extends JpaRepository<PointPromotion, Long> {
}
