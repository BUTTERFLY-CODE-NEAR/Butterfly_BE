package com.codenear.butterfly.notify.alarm.infrastructure;

import com.codenear.butterfly.notify.alarm.domain.Alarm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByMemberId(Long memberId);
}
