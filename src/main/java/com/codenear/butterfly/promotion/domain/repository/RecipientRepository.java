package com.codenear.butterfly.promotion.domain.repository;

import com.codenear.butterfly.promotion.domain.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}
