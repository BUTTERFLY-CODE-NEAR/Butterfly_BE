package com.codenear.butterfly.payment.kakaoPay.application;

import com.codenear.butterfly.address.domain.repository.AddressRepository;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.application.PaymentCancel;
import com.codenear.butterfly.payment.application.PaymentService;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.handler.CancelFreePaymentHandler;
import com.codenear.butterfly.payment.domain.dto.handler.CancelHandler;
import com.codenear.butterfly.payment.domain.dto.handler.CancelPaymentHandler;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.CancelResponseDTO;
import com.codenear.butterfly.payment.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class KakaoCancelService extends PaymentService implements PaymentCancel {
    private final OrderDetailsRepository orderDetailsRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;

    public KakaoCancelService(SinglePaymentRepository singlePaymentRepository,
                              AddressRepository addressRepository,
                              OrderDetailsRepository orderDetailsRepository,
                              MemberRepository memberRepository,
                              ProductInventoryRepository productInventoryRepository,
                              PaymentRedisRepository paymentRedisRepository,
                              PointRepository pointRepository,
                              ApplicationEventPublisher applicationEventPublisher,
                              FCMFacade fcmFacade,
                              CancelPaymentRepository cancelPaymentRepository,
                              KakaoPaymentUtil<Object> kakaoPaymentUtil) {
        super(singlePaymentRepository, addressRepository, orderDetailsRepository, memberRepository, productInventoryRepository, paymentRedisRepository, pointRepository, applicationEventPublisher, fcmFacade, cancelPaymentRepository);
        this.orderDetailsRepository = orderDetailsRepository;
        this.kakaoPaymentUtil = kakaoPaymentUtil;
    }

    @Override
    public void cancel(CancelRequestDTO cancelRequestDTO) {

        OrderDetails orderDetails = orderDetailsRepository.findByOrderCode(cancelRequestDTO.getOrderCode());

        CancelHandler handler;
        if (orderDetails.getTotal() != 0) {
            Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayCancelParameters(orderDetails, cancelRequestDTO);
            CancelResponseDTO cancelResponseDTO = kakaoPaymentUtil.sendRequest("/cancel", parameters, CancelResponseDTO.class);

            handler = new CancelPaymentHandler(cancelResponseDTO, orderDetails);
        } else {
            handler = new CancelFreePaymentHandler(orderDetails);
        }

        super.processPaymentCancel(handler, orderDetails.getMember().getId());
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }
}
