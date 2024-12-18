package com.codenear.butterfly.notify.alarm.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmResponse;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmService alarmService;

    @Test
    void 유저_아이디로_알림_목록을_반환한다() {
        // given
        Long memberId = 1L;
        NotifyMessage message = mock(NotifyMessage.class);
        Member member = mock(Member.class);

        Alarm alarm = Alarm.builder()
                .id(memberId)
                .notifyMessage(message)
                .member(member)
                .isNew(true)
                .build();

        List<Alarm> alarms = List.of(alarm);

        when(alarmRepository.findByMemberId(memberId))
                .thenReturn(alarms);

        // when
        AlarmsResponse result = alarmService.getAlarmsByMemberId(memberId);

        // then
        verify(alarmRepository).findByMemberId(memberId);
        assertThat(result.alarmResponses())
                .hasSize(alarms.size())
                .extracting(AlarmResponse::id)
                .containsExactly(memberId);
    }
}