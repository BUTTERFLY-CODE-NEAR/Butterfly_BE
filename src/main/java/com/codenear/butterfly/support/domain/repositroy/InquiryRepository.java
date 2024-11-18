package com.codenear.butterfly.support.domain.repositroy;

import com.codenear.butterfly.support.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
