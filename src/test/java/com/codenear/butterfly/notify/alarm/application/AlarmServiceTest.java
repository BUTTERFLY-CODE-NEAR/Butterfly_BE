package com.codenear.butterfly.notify.alarm.application;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmResponse;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRedisRepository;
import com.codenear.butterfly.notify.alarm.infrastructure.AlarmRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.notify.fcm.application.FCMMessageService;
import com.codenear.butterfly.notify.fcm.application.FCMTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private AlarmRedisRepository alarmRedisRepository;

    @Mock
    private FCMMessageService fcmMessageService;

    @Mock
    private FCMTokenService fcmTokenService;

    @InjectMocks
    private FCMFacade fcmFacade;

    @InjectMocks
    private AlarmService alarmService;

    private Long memberId;
    private NotifyMessage message;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        message = mock(NotifyMessage.class);
    }

    @Test
    void 유저_아이디로_알림_목록을_반환한다() {
        // given
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

    @Test
    void 개인_알림을_발행하면_미확인_알림_개수가_증가한다() {
        //given
        AtomicLong unreadCount = new AtomicLong(0L);

        doAnswer(invocation -> {
            unreadCount.incrementAndGet();
            return null;
        }).when(alarmRedisRepository).incrementAlarmByMember(memberId);

        when(alarmService.getAlarmCountByMember(memberId))
                .thenAnswer(invocation -> unreadCount.get());

        //when
        fcmFacade.sendMessage(message, memberId);
        alarmService.getAlarmCountByMember(memberId);

        //then
        verify(alarmRedisRepository, times(1)).incrementAlarmByMember(memberId);
        verify(alarmRedisRepository, times(1)).getTotalUnreadAlarms(memberId);
        assertThat(alarmService.getAlarmCountByMember(memberId).unreadAlarmCount()).isEqualTo(1L);
    }

    @Test
    void 전역_알림을_발송하면_미확인_알림_개수가_증가한다() {
        // given
        String topic = "topic";
        AtomicLong unreadCount = new AtomicLong(0L);

        doAnswer(invocation -> {
            unreadCount.incrementAndGet();
            return null;
        }).when(alarmRedisRepository).incrementBroadcastVersion();

        when(alarmService.getAlarmCountByMember(memberId))
                .thenAnswer(invocation -> unreadCount.get());

        //when
        fcmFacade.sendTopicMessage(message, topic);

        //then
        verify(alarmRedisRepository, times(1)).incrementBroadcastVersion();
        assertThat(alarmService.getAlarmCountByMember(memberId).unreadAlarmCount()).isEqualTo(1L);
    }

    @Test
    void 알림을_확인하면_미확인_알림_개수가_초기화된다() {
        //given
        AtomicLong unreadCount = new AtomicLong(0L);

        doAnswer(invocation -> {
            unreadCount.incrementAndGet();
            return null;
        }).when(alarmRedisRepository).incrementAlarmByMember(memberId);

        doAnswer(invocation -> {
            unreadCount.set(0L);
            return null;
        }).when(alarmRedisRepository).readAlarm(memberId);

        when(alarmService.getAlarmCountByMember(memberId))
                .thenAnswer(invocation -> unreadCount.get());

        //when (알림 발송)
        fcmFacade.sendMessage(message, memberId);
        alarmService.getAlarmCountByMember(memberId);

        //then (알림 발송)
        verify(alarmRedisRepository, times(1)).incrementAlarmByMember(memberId);
        verify(alarmRedisRepository, times(1)).getTotalUnreadAlarms(memberId);
        assertThat(alarmService.getAlarmCountByMember(memberId).unreadAlarmCount()).isEqualTo(1L);

        //when (알림 확인)
        alarmService.getAlarmsByMemberId(memberId);

        //then(알림 확인)
        verify(alarmRedisRepository, times(1)).readAlarm(memberId);
        verify(alarmRedisRepository, times(2)).getTotalUnreadAlarms(memberId);
        assertThat(alarmService.getAlarmCountByMember(memberId).unreadAlarmCount()).isEqualTo(0L);
    }
}