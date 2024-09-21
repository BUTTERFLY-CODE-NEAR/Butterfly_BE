package com.codenear.butterfly.support.domain;

import com.codenear.butterfly.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMember(Member member);
}
