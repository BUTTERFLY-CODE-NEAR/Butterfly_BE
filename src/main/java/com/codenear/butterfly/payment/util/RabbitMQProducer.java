package com.codenear.butterfly.payment.util;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing-key.name}")
    private String routingKey;

    public <T> void sendMessage(T message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, m -> {
            // 메시지의 실제 클래스 이름을 헤더에 설정
            m.getMessageProperties().setHeader("__TypeId__", message.getClass().getName());
            return m;
        });
    }
}
