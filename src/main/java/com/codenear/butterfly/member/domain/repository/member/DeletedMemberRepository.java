package com.codenear.butterfly.member.domain.repository.member;

import com.codenear.butterfly.member.domain.DeletedMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long> {
    void deleteByMember_Email(String email);
}
