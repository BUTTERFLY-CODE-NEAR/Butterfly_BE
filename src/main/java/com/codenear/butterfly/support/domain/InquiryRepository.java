package com.codenear.butterfly.support.domain;

import com.codenear.butterfly.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMemberOrderByCreatedAtDesc(Member member);
}
