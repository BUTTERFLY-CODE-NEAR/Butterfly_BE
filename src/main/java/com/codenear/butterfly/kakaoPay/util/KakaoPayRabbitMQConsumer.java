package com.codenear.butterfly.kakaoPay.util;

import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryDecreaseMessageDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryIncreaseMessageDTO;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "${rabbitmq.queue.name}", ackMode = "MANUAL")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoPayRabbitMQConsumer {
    private final int DEFAULT_MAX_PURCHASE_NUM = 5;
    private final ProductInventoryRepository productInventoryRepository;

    /**
     * RabbitMQ에서 재고 감소 메시지를 받아 DB 재고를 차감
     *
     * @param message RabbitMQ queue에서 가져온 message
     */
    @RabbitHandler
    public void handleInventoryDecrease(InventoryDecreaseMessageDTO message, Channel channel, Message rabbitMessage) {
        try {
            ProductInventory product = productInventoryRepository.findProductByProductName(message.productName());
            // 예약된 재고를 최종 차감
            product.decreaseQuantity(message.quantity());
            product.increasePurchaseParticipantCount(message.quantity(), DEFAULT_MAX_PURCHASE_NUM);

            // 메시지 처리 성공 시 ack
            channel.basicAck(rabbitMessage.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            restoreMessage("decrease", message.productName(), message.quantity(), channel, rabbitMessage);
        }
    }

    /**
     * RabbitMQ에서 재고 증가 메시지를 받아 DB 재고를 증가
     *
     * @param message RabbitMQ queue에서 가져온 message
     */
    @RabbitHandler
    public void handleInventoryIncrease(InventoryIncreaseMessageDTO message, Channel channel, Message rabbitMessage) {
        try {
            ProductInventory product = productInventoryRepository.findProductByProductName(message.productName());
            // 취소된 주문 재고 추가
            product.increaseQuantity(message.quantity());
            product.decreasePurchaseParticipantCount(message.quantity(), DEFAULT_MAX_PURCHASE_NUM);

            // 메시지 처리 성공 시 ack
            channel.basicAck(rabbitMessage.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            restoreMessage("increase", message.productName(), message.quantity(), channel, rabbitMessage);
        }
    }

    /**
     * 메세지 처리 실패 시 해당 메세지를 다시 queue에 삽입
     *
     * @param type          오류 시 사용할 타입 (increase, decrease)
     * @param productName   상품 이름
     * @param quantity      구매 개수
     * @param channel       rabbitmq client 채널
     * @param rabbitMessage 수신 메세지 (Body, MessageProperties, contentType, etc...)
     */
    private void restoreMessage(String type, String productName, int quantity, Channel channel, Message rabbitMessage) {
        try {
            channel.basicNack(rabbitMessage.getMessageProperties().getDeliveryTag(), false, true);
        } catch (Exception ex) {
            switch (type) {
                case "increase" -> log.error("increase : {}, {} - 메세지 수신 실패", productName, quantity);
                case "decrease" -> log.error("decrease : {}, {} - 메세지 수신 실패", productName, quantity);
            }
        }
    }
}

