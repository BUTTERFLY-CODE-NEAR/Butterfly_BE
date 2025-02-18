package com.codenear.butterfly.kakaoPay.util;

import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryDecreaseMessageDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryIncreaseMessageDTO;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "${rabbitmq.queue.name}")
@RequiredArgsConstructor
@Transactional
public class KakaoPayRabbitMQConsumer {
    private final ProductInventoryRepository productInventoryRepository;

    /**
     * RabbitMQ에서 재고 감소 메시지를 받아 DB 재고를 차감
     *
     * @param message RabbitMQ queue에서 가져온 message
     */
    @RabbitHandler
    public void handleInventoryDecrease(InventoryDecreaseMessageDTO message) {
        ProductInventory product = productInventoryRepository.findProductByProductName(message.productName());

        // 예약된 재고를 최종 차감
        product.decreaseQuantity(message.quantity());
        product.increasePurchaseParticipantCount(message.quantity());
    }

    /**
     * RabbitMQ에서 재고 증가 메시지를 받아 DB 재고를 증가
     *
     * @param message RabbitMQ queue에서 가져온 message
     */
    @RabbitHandler
    public void handleInventoryIncrease(InventoryIncreaseMessageDTO message) {
        ProductInventory product = productInventoryRepository.findProductByProductName(message.productName());

        // 취소된 주문 재고 추가
        product.increaseQuantity(message.quantity());
        product.decreasePurchaseParticipantCount(message.quantity());
    }
}

