package com.codenear.butterfly.fcm.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMRepository extends JpaRepository<FCM, Long> {
    List<FCM> findByMemberId(Long memberId);
}
