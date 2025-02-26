package com.codenear.butterfly.kakaoPay.util;

import com.codenear.butterfly.kakaoPay.application.SinglePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KakaoPayRedisExpiredKeyListener extends KeyExpirationEventMessageListener {
    private final SinglePaymentService singlePaymentService;

    @Autowired
    public KakaoPayRedisExpiredKeyListener(RedisMessageListenerContainer listenerContainer, SinglePaymentService singlePaymentService) {
        super(listenerContainer);
        this.singlePaymentService = singlePaymentService;
    }

    /**
     * 예약된 재고에 대해서 key의 시간이 만료되었을 때 재고를 반환한다.
     *
     * @param message TTL이 만료된 key
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (message.toString().startsWith("reserve:")) {
            Pattern keyPattern = Pattern.compile("reserve:product:(.*):quantity:(.*):member:(.*)");
            Matcher matcher = keyPattern.matcher(message.toString().trim());
            if (matcher.matches()) {
                String productName = matcher.group(1);
                int quantity = Integer.parseInt(matcher.group(2));
                Long memberId = Long.parseLong(matcher.group(3));
                singlePaymentService.restoreQuantity(memberId, productName, quantity);
            }
        }
    }
}
