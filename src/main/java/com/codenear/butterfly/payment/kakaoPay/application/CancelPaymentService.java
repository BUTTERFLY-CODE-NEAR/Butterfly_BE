package com.codenear.butterfly.payment.kakaoPay.application;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.CancelPayment;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import com.codenear.butterfly.payment.domain.dto.rabbitmq.InventoryIncreaseMessageDTO;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.handler.CancelFreePaymentHandler;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.handler.CancelHandler;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.handler.CancelPaymentHandler;
import com.codenear.butterfly.payment.kakaoPay.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.payment.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.codenear.butterfly.notify.NotifyMessage.ORDER_CANCELED;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelPaymentService {
    private final CancelPaymentRepository cancelPaymentRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;
    private final PaymentRedisRepository kakaoPaymentRedisRepository;
    private final PointRepository pointRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FCMFacade fcmFacade;

    public void cancelKakaoPay(CancelRequestDTO cancelRequestDTO) {

        OrderDetails orderDetails = orderDetailsRepository.findByOrderCode(cancelRequestDTO.getOrderCode());

        CancelHandler handler;
        if (orderDetails.getTotal() != 0) {
            Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayCancelParameters(orderDetails, cancelRequestDTO);
            CancelResponseDTO cancelResponseDTO = kakaoPaymentUtil.sendRequest("/cancel", parameters, CancelResponseDTO.class);

            handler = new CancelPaymentHandler(cancelResponseDTO, orderDetails);
        } else {
            handler = new CancelFreePaymentHandler(orderDetails);
        }

        processPaymentCancel(handler);
        fcmFacade.sendMessage(ORDER_CANCELED, orderDetails.getMember().getId());
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
