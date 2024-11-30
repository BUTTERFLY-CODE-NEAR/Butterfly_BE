package com.codenear.butterfly.product.domain.repository;

import com.codenear.butterfly.product.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findKeywordByKeyword(String keyword);
}
