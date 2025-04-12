package com.codenear.butterfly.payment.kakaoPay.application;

import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.global.util.HashMapUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.application.PaymentService;
import com.codenear.butterfly.payment.domain.KakaoPayRedisField;
import com.codenear.butterfly.payment.domain.dto.OrderType;
import com.codenear.butterfly.payment.domain.dto.PaymentStatus;
import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ReadyResponseDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.handler.ApprovePaymentHandler;
import com.codenear.butterfly.payment.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class SinglePaymentService extends PaymentService implements KakaoPaymentService {
    private final PaymentRedisRepository kakaoPaymentRedisRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;
    private final ProductInventoryRepository productInventoryRepository;

    public SinglePaymentService(SinglePaymentRepository singlePaymentRepository,
                                AddressRepository addressRepository,
                                OrderDetailsRepository orderDetailsRepository,
                                MemberRepository memberRepository,
                                ProductInventoryRepository productInventoryRepository,
                                PaymentRedisRepository kakaoPaymentRedisRepository,
                                PointRepository pointRepository,
                                ApplicationEventPublisher applicationEventPublisher,
                                KakaoPaymentUtil<Object> kakaoPaymentUtil,
                                FCMFacade fcmFacade) {
        super(singlePaymentRepository, addressRepository, orderDetailsRepository, memberRepository, productInventoryRepository, kakaoPaymentRedisRepository, pointRepository, applicationEventPublisher, fcmFacade);
        this.kakaoPaymentUtil = kakaoPaymentUtil;
        this.kakaoPaymentRedisRepository = kakaoPaymentRedisRepository;
        this.productInventoryRepository = productInventoryRepository;
    }

    @Transactional
    @Override
    public ReadyResponseDTO paymentReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType) {
        Member member = super.loadByMember(memberId);
        super.validateRemainingPointForPurchase(member, paymentRequestDTO.getPoint());
        String partnerOrderId = UUID.randomUUID().toString();
        // 재고 예약
        kakaoPaymentRedisRepository.reserveStock(paymentRequestDTO.getProductName(), paymentRequestDTO.getQuantity(), partnerOrderId);

        ReadyResponseDTO kakaoPayReady = null;
        if (paymentRequestDTO.getTotal() != 0) {
            Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayReadyParameters(paymentRequestDTO, memberId, partnerOrderId);
            kakaoPayReady = kakaoPaymentUtil.sendRequest("/ready", parameters, ReadyResponseDTO.class);
        }

        String tid = kakaoPayReady != null ? kakaoPayReady.getTid() : null;

        Map<String, String> fields = getKakaoPayReadyRedisFields(partnerOrderId, orderType, tid, paymentRequestDTO);
        kakaoPaymentRedisRepository.addMultipleToHashSet(memberId, fields);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.READY.name());

        if (kakaoPayReady == null) {
            super.approveFreeResponse(memberId, paymentRequestDTO, partnerOrderId);
        }

        return kakaoPayReady;
    }

    @Transactional
    @Override
    public void paymentApprove(String pgToken, Long memberId) {
        String orderId = kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.ORDER_ID.getFieldName());
        String orderTypeString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.ORDER_TYPE.getFieldName());
        OrderType orderType = OrderType.fromType(orderTypeString);
        Long addressId = super.parsingStringToLong(memberId, KakaoPayRedisField.ADDRESS_ID.getFieldName());
        String optionName = kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.OPTION_NAME.getFieldName());

        Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayApproveParameters(memberId, orderId,
                kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.TRANSACTION_ID.getFieldName()), pgToken);

        ApproveResponseDTO approveResponseDTO = kakaoPaymentUtil.sendRequest("/approve", parameters, ApproveResponseDTO.class);
        ProductInventory product = productInventoryRepository.findProductByProductName(approveResponseDTO.getItem_name());

        int usePoint = super.parsingStringToInt(memberId, KakaoPayRedisField.POINT.getFieldName());
        super.processPaymentSuccess(memberId, orderType, addressId, optionName, product, new ApprovePaymentHandler(approveResponseDTO, usePoint));
    }

    @Override
    public void cancelPayment(Long memberId, String productName, int quantity) {
        super.restoreQuantity(productName, quantity, kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.ORDER_ID.getFieldName()));
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.CANCEL.name());
        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
    }

    @Override
    public void failPayment(Long memberId, String productName, int quantity) {
        super.restoreQuantity(productName, quantity, kakaoPaymentRedisRepository.getHashFieldValue(memberId, KakaoPayRedisField.ORDER_ID.getFieldName()));
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.FAIL.name());
        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
    }

    /**
     * 카카오페이 결제 준비 단계에서 Redis에 저장할 필드를 생성
     *
     * @param partnerOrderId    파트너사 주문 ID
     * @param orderType         주문 타입
     * @param tid               카카오페이 트랜잭션 ID
     * @param paymentRequestDTO 결제 요청 정보를 담고 있는 객체 (BasePaymentRequestDTO 타입)
     * @return Redis에 저장할 필드 값들을 키-값 쌍으로 담고 있는 Map 객체
     */

    private Map<String, String> getKakaoPayReadyRedisFields(
            final String partnerOrderId,
            final String orderType,
            final String tid,
            final BasePaymentRequestDTO paymentRequestDTO) {

        Map<String, String> fields = new HashMapUtil<>();
        fields.put(KakaoPayRedisField.ORDER_ID.getFieldName(), partnerOrderId);
        fields.put(KakaoPayRedisField.TRANSACTION_ID.getFieldName(), tid);
        fields.put(KakaoPayRedisField.ORDER_TYPE.getFieldName(), orderType);
        fields.put(KakaoPayRedisField.OPTION_NAME.getFieldName(), paymentRequestDTO.getOptionName());
        fields.put(KakaoPayRedisField.POINT.getFieldName(), String.valueOf(paymentRequestDTO.getPoint()));

        if (paymentRequestDTO instanceof DeliveryPaymentRequestDTO deliveryPaymentRequestDTO) {
            fields.put(KakaoPayRedisField.ADDRESS_ID.getFieldName(), deliveryPaymentRequestDTO.getAddressId().toString());
            fields.put(KakaoPayRedisField.DELIVER_DATE.getFieldName(), deliveryPaymentRequestDTO.deliverDateFormat());
        }

        if (paymentRequestDTO instanceof PickupPaymentRequestDTO pickupPaymentRequestDTO) {
            String pickupDate = pickupPaymentRequestDTO.getPickupDate().toString();
            String pickupTime = pickupPaymentRequestDTO.getPickupTime().toString();

            fields.put(KakaoPayRedisField.PICKUP_PLACE.getFieldName(), pickupPaymentRequestDTO.getPickupPlace());
            fields.put(KakaoPayRedisField.PICKUP_DATE.getFieldName(), pickupDate);
            fields.put(KakaoPayRedisField.PICKUP_TIME.getFieldName(), pickupTime);
        }
        return fields;
    }
}