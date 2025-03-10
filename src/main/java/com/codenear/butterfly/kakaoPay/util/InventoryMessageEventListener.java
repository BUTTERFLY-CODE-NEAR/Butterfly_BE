package com.codenear.butterfly.kakaoPay.util;

import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryMessageEventListener {
    private final KakaoPayRabbitMQProducer rabbitMQProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleInventoryDecreaseEvent(InventoryMessage message) {
        rabbitMQProducer.sendMessage(message);
    }
}
