package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler.CancelFreePaymentHandler;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler.CancelHandler;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.handler.CancelPaymentHandler;
import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryIncreaseMessageDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.KakaoPaymentRedisRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.codenear.butterfly.notify.NotifyMessage.ORDER_CANCELED;

import java.util.Map;

import static com.codenear.butterfly.notify.NotifyMessage.PRODUCT_ARRIVAL;

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
    private final FCMFacade fcmFacade;

    public void cancelKakaoPay(CancelRequestDTO cancelRequestDTO) {

        OrderDetails orderDetails = orderDetailsRepository.findByOrderCode(cancelRequestDTO.getOrderCode());

        CancelHandler handler;
        if (orderDetails.getTotal() != 0) {
            Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayCancelParameters(orderDetails, cancelRequestDTO);
            CancelResponseDTO cancelResponseDTO = kakaoPaymentUtil.sendRequest("/cancel", parameters, CancelResponseDTO.class);

            handler = new CancelPaymentHandler(cancelResponseDTO);
        } else {
            handler = new CancelFreePaymentHandler(orderDetails);
        }

        processPaymentCancel(handler);
    }

    /**
     * 주문 취소 처리 공통 로직
     *
     * @param handler 결제 응답 객체 (CancelResponseDTO 또는 OrderDetails)
     */
    private void processPaymentCancel(CancelHandler handler) {
        CancelPayment cancelPayment = handler.createCancelPayment();

        handler.getOrderDetails().updateOrderStatus(OrderStatus.CANCELED);

        kakaoPaymentRedisRepository.restoreStockOnOrderCancellation(handler.getProductName(), handler.getQuantity());

        increaseUsePoint(handler.getOrderDetails().getMember(), handler.getRestorePoint());

        cancelPaymentRepository.save(cancelPayment);
        fcmFacade.sendMessage(ORDER_CANCELED, orderDetails.getMember().getId());

        InventoryIncreaseMessageDTO message = new InventoryIncreaseMessageDTO(handler.getProductName(), handler.getQuantity());
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
