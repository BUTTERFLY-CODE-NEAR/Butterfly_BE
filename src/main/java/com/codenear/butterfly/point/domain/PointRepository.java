package com.codenear.butterfly.point.domain;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.product.domain.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByMember(Member member);

    @Modifying
    @Query("UPDATE Point p SET p.point = p.point + :amount WHERE p.member.id = :memberId")
    void increasePointByMemberId(@Param("memberId") Long memberId, @Param("amount") int amount);
}
