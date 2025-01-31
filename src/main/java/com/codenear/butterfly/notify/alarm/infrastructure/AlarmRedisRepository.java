package com.codenear.butterfly.notify.alarm.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AlarmRedisRepository {
    private final String ALARM_REDIS_PREFIX = "unread_alarm:";
    private final String BROADCAST_COUNT_KEY = "broadcast_count";
    private final String LAST_READ_BROADCAST_PREFIX = "last_read_broadcast:";
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 알림이 생성되었을 때 미확인 알림 개수 증가
     * 초기 TTL을 1주일로 설정 이유 : 앱 설치 후 7일 경과 시 평균 앱 이탈률은 87%이며, 30일 후에는 94%정도의 이탈률
     * 출처 : NNT-Consulting 블로
     *
     * @param memberId 멤버 아이디
     */
    public void incrementAlarmByMember(Long memberId) {
        String key = ALARM_REDIS_PREFIX + memberId;

        redisTemplate.opsForValue().increment(key);
        //TODO: 추후 이탈률 계산해서 TTL 재설정 하기
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 알림을 확인했다면 지금까지 읽지 않은 알림은 모두 읽음 처리
     *
     * @param memberId 멤버 아이디
     */
    public void readAlarm(Long memberId) {
        redisTemplate.delete(ALARM_REDIS_PREFIX + memberId);
        setLastReadBroadcast(memberId);
    }

    /**
     * 전역 브로드캐스트 발행 개수 증가
     */
    public void incrementBroadcastVersion() {
        redisTemplate.opsForValue().increment(BROADCAST_COUNT_KEY);
    }

    /**
     * 총 미확인 알림 개수 계산 (개별 + 브로드캐스트)
     *
     * @param memberId 사용자 ID
     * @return 총 미확인 알림 개수
     */
    public Long getTotalUnreadAlarms(Long memberId) {
        String memberUnreadCount = getAlarmCount(memberId);
        Long userUnread = memberUnreadCount != null ? Long.parseLong(memberUnreadCount) : 0L;
        Long broadcastUnread = getUnreadBroadcastCount(memberId);
        return userUnread + broadcastUnread;
    }

    /**
     * 전체 브로드캐스트 메시지의 미확인 개수 계산
     *
     * @param memberId 사용자 ID
     * @return 미확인 브로드캐스트 메시지 개수
     */
    private Long getUnreadBroadcastCount(Long memberId) {
        Long currentBroadcastCount = getCurrentBroadcastVersion();
        Long memberLastReadNumber = getLastReadBroadcast(memberId);
        return currentBroadcastCount - memberLastReadNumber;
    }

    /**
     * 사용자별 마지막 읽은 브로드캐스트 버전 조회
     *
     * @param memberId 사용자 ID
     * @return 마지막 읽은 브로드캐스트 버전
     */
    private Long getLastReadBroadcast(Long memberId) {
        String lastReadBroadcastNumber = redisTemplate.opsForValue().get(LAST_READ_BROADCAST_PREFIX + memberId);
        return lastReadBroadcastNumber != null ? Long.parseLong(lastReadBroadcastNumber) : 0L;
    }

    /**
     * 전역 알림을 제외한 알림개수
     *
     * @param memberId 멤버 아이디
     * @return 사용자 미확인 알림 개수
     */
    private String getAlarmCount(Long memberId) {
        return redisTemplate.opsForValue().get(ALARM_REDIS_PREFIX + memberId);
    }

    /**
     * 현재 브로드캐스트 발행 개수 조회
     *
     * @return 현재 브로드캐스트 버전
     */
    private Long getCurrentBroadcastVersion() {
        String broadcastCount = redisTemplate.opsForValue().get(BROADCAST_COUNT_KEY);
        return broadcastCount != null ? Long.parseLong(broadcastCount) : 0L;
    }

    /**
     * 사용자별 마지막 읽은 브로드캐스트 버전 설정
     *
     * @param memberId 사용자 ID
     */
    private void setLastReadBroadcast(Long memberId) {
        redisTemplate.opsForValue().set(LAST_READ_BROADCAST_PREFIX + memberId, getCurrentBroadcastVersion().toString());
    }
}
