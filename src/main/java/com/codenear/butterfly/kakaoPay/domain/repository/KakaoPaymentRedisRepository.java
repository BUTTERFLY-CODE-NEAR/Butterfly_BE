package com.codenear.butterfly.kakaoPay.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PAYMENT_STATUS;

@Repository
@RequiredArgsConstructor
public class KakaoPaymentRedisRepository {

    private static final String PAYMENT_HASH_KEY_PREFIX = "pay:";
    private static final int TIME_TO_LIVE_MINUTE = 15;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis Hash에 여러개의 데이터를 한번에 저장
     *
     * @param memberId 멤버 아이디
     * @param fields 여러개의 필드와 값이 저장된 맵
     */
    public void addMultipleToHashSet(final Long memberId, final Map<String, String> fields) {
        // 여러 값을 한 번에 저장
        redisTemplate.opsForHash().putAll(PAYMENT_HASH_KEY_PREFIX + memberId, fields);
        ensureTTL();
    }

    /**
     * Redis Hash에서 특정 필드의 값을 가져오는 메서드
     *
     * @param memberId 멤버 아이디
     * @param field 필드 이름
     * @return 필드 값
     */
    public String getHashFieldValue(final Long memberId, final String field) {
        return (String) redisTemplate.opsForHash().get(PAYMENT_HASH_KEY_PREFIX + memberId, field);
    }

    /**
     * 멤버의 Redis Hash Table 제거
     *
     * @param memberId 멤버 아이디
     */
    public void removeHashTableKey(final Long memberId) {
        redisTemplate.delete(PAYMENT_HASH_KEY_PREFIX + memberId);
    }

    /**
     * TTL 시간 설정
     */
    private void ensureTTL() {
        // Hash Key가 처음 생성된 경우에만 TTL 설정
        if (Boolean.FALSE.equals(redisTemplate.hasKey(PAYMENT_HASH_KEY_PREFIX))) {
            // TTL 15분 설정 (kakao pay API가 호출 후 생성되는 tid의 유효기간은 15분 이기에 15분으로 설정)
            redisTemplate.expire(PAYMENT_HASH_KEY_PREFIX, TIME_TO_LIVE_MINUTE, TimeUnit.MILLISECONDS);
        }
    }

    public void savePaymentStatus(Long memberId, String status) {
        String key = PAYMENT_STATUS.getFieldName() + memberId;
        redisTemplate.opsForValue().set(key, status, 30, TimeUnit.MINUTES);
    }

    public String getPaymentStatus(Long memberId) {
        String key = PAYMENT_STATUS.getFieldName() + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    public void removePaymentStatus(String key) {
        redisTemplate.delete(key);
    }
}
