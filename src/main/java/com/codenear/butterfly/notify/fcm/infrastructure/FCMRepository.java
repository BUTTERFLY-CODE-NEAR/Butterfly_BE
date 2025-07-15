package com.codenear.butterfly.notify.fcm.infrastructure;

import com.codenear.butterfly.notify.fcm.domain.FCM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FCMRepository extends JpaRepository<FCM, Long> {
    List<FCM> findByMemberId(Long memberId);

    List<FCM> findByToken(String token);

    void deleteByToken(String token);
}
