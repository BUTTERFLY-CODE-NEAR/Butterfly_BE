package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.CanceledAmount;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryIncreaseMessageDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.KakaoPaymentRedisRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelPaymentService {
    private final CancelPaymentRepository cancelPaymentRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;
    private final KakaoPaymentRedisRepository kakaoPaymentRedisRepository;
    private final PointRepository pointRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void cancelKakaoPay(CancelRequestDTO cancelRequestDTO) {

        OrderDetails orderDetails = orderDetailsRepository.findByOrderCode(cancelRequestDTO.getOrderCode());

        Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayCancelParameters(orderDetails, cancelRequestDTO);

        CancelResponseDTO cancelResponseDTO = kakaoPaymentUtil.sendRequest("/cancel", parameters, CancelResponseDTO.class);

        CancelPayment cancelPayment = CancelPayment.builder().cancelResponseDTO(cancelResponseDTO).build();

        CanceledAmount canceledAmount = CanceledAmount.builder().cancelResponseDTO(cancelResponseDTO).build();
        cancelPayment.addCanceledAmount(canceledAmount);

        orderDetails.updateOrderStatus(OrderStatus.CANCELED);
        kakaoPaymentRedisRepository.restoreStockOnOrderCancellation(orderDetails.getProductName(), orderDetails.getQuantity());
        increaseUsePoint(orderDetails.getMember(), orderDetails.getPoint());
        cancelPaymentRepository.save(cancelPayment);

        // DB 재고 업데이트를 위해 RabbitMQ 메시지 전송
        InventoryIncreaseMessageDTO message = new InventoryIncreaseMessageDTO(orderDetails.getProductName(), orderDetails.getQuantity());
        applicationEventPublisher.publishEvent(message);
    }

    public void increaseUsePoint(Member member, int usePoint) {
        Point point = pointRepository.findByMember(member)
                .orElseGet(() -> {
                    Point newPoint = Point.createPoint()
                            .member(member)
                            .build();
                    return pointRepository.save(newPoint);
                });

        point.increasePoint(usePoint);
    }
}
