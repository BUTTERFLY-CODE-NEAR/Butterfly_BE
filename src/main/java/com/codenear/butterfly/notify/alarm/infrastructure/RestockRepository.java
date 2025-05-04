package com.codenear.butterfly.notify.alarm.infrastructure;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.alarm.domain.Restock;
import com.codenear.butterfly.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestockRepository extends JpaRepository<Restock, Long> {
    Restock findByMemberAndProduct(Member member, Product product);

    boolean existsByMemberAndProduct(Member member, Product product);
}
