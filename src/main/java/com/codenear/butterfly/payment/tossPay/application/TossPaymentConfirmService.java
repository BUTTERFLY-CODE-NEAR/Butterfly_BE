package com.codenear.butterfly.payment.tossPay.application;

import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.application.PaymentService;
import com.codenear.butterfly.payment.domain.dto.PaymentStatus;
import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import com.codenear.butterfly.payment.tossPay.util.TossPaymentUtil;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class TossPaymentConfirmService extends PaymentService implements TossPaymentService {
    private final PaymentRedisRepository paymentRedisRepository;
    private final TossPaymentUtil<Object> tossPaymentUtil;

    public TossPaymentConfirmService(SinglePaymentRepository singlePaymentRepository, AddressRepository addressRepository, OrderDetailsRepository orderDetailsRepository, MemberRepository memberRepository, ProductInventoryRepository productInventoryRepository, PaymentRedisRepository kakaoPaymentRedisRepository, PointRepository pointRepository, ApplicationEventPublisher applicationEventPublisher, FCMFacade fcmFacade, TossPaymentUtil<Object> tossPaymentUtil) {
        super(singlePaymentRepository, addressRepository, orderDetailsRepository, memberRepository, productInventoryRepository, kakaoPaymentRedisRepository, pointRepository, applicationEventPublisher, fcmFacade);
        this.paymentRedisRepository = kakaoPaymentRedisRepository;
        this.tossPaymentUtil = tossPaymentUtil;

    }

    /**
     * Confirm전 재고 예약 및 검증 데이터 저장
     *
     * @param memberId              사용자 아이디
     * @param basePaymentRequestDTO 결제 데이터
     */
    @Transactional
    public Object paymentReady(BasePaymentRequestDTO basePaymentRequestDTO, Long memberId, String OrderType) {
        Member member = super.loadByMember(memberId);
        super.validateRemainingPointForPurchase(member, basePaymentRequestDTO.getPoint());

        String uniqueUUID = UUID.randomUUID().toString();
        Map<String, String> parameters = tossPaymentUtil.preConfirmParameter(basePaymentRequestDTO.getQuantity(), basePaymentRequestDTO.getTotal());
        paymentRedisRepository.addMultipleToHashSet(memberId, parameters);

        paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.READY.name());
        paymentRedisRepository.reserveStock(basePaymentRequestDTO.getProductName(), basePaymentRequestDTO.getQuantity(), uniqueUUID);

        return null;
    }

    public ConfirmResponseDTO confirm(String paymentKey, String orderId, int amount) {
        Map<String, Object> parameters = tossPaymentUtil.confirmParameter(paymentKey, orderId, amount);

        ConfirmResponseDTO confirmResponse = tossPaymentUtil.sendRequest("/confirm", parameters, ConfirmResponseDTO.class);
        return confirmResponse;
    }

    @Override
    public void paymentApprove(String paymentKey, String orderId, int amount) {

    }

    @Override
    public void cancelPayment(Long memberId, String productName, int quantity) {

    }
}
