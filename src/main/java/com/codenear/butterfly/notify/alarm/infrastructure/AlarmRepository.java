package com.codenear.butterfly.notify.alarm.infrastructure;

import com.codenear.butterfly.notify.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    boolean existsByMemberIdAndIsNewTrue(Long memberId);

    @Modifying
    @Query("UPDATE Alarm alarm SET alarm.isNew = false WHERE alarm.member.id = :memberId AND alarm.isNew = true")
    void markAllAsReadByMemberId(@Param("memberId") Long memberId);
}
