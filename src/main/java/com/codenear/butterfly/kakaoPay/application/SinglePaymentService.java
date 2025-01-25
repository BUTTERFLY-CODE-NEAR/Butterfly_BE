package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.HashMapUtil;
import com.codenear.butterfly.kakaoPay.domain.Amount;
import com.codenear.butterfly.kakaoPay.domain.CardInfo;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.PaymentMethod;
import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderType;
import com.codenear.butterfly.kakaoPay.domain.dto.PaymentStatus;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ReadyResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.KakaoPaymentRedisRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.kakaoPay.exception.KakaoPayException;
import com.codenear.butterfly.kakaoPay.util.KakaoPaymentUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.ADDRESS_ID;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.OPTION_NAME;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.ORDER_ID;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.ORDER_TYPE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PAYMENT_STATUS;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_DATE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_PLACE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_TIME;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.TRANSACTION_ID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SinglePaymentService {

    private final SinglePaymentRepository singlePaymentRepository;
    private final AddressRepository addressRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final MemberRepository memberRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final KakaoPaymentRedisRepository kakaoPaymentRedisRepository;
    private final PointRepository pointRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;

    public ReadyResponseDTO kakaoPayReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType) {
        String partnerOrderId = UUID.randomUUID().toString();

        Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayReadyParameters(paymentRequestDTO, memberId, partnerOrderId);
        ReadyResponseDTO kakaoPayReady = kakaoPaymentUtil.sendRequest("/ready", parameters, ReadyResponseDTO.class);

        String tid = kakaoPayReady != null ? kakaoPayReady.getTid() : null;

        Map<String, String> fields = getKakaoPayReadyRedisFields(partnerOrderId, orderType, tid, paymentRequestDTO);
        kakaoPaymentRedisRepository.addMultipleToHashSet(memberId, fields);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.READY.name());

        return kakaoPayReady;
    }

    @Transactional
    public void approveResponse(String pgToken, Long memberId) {
        String orderId = kakaoPaymentRedisRepository.getHashFieldValue(memberId, ORDER_ID.getFieldName());
        String transactionId = kakaoPaymentRedisRepository.getHashFieldValue(memberId, TRANSACTION_ID.getFieldName());
        String orderTypeString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, ORDER_TYPE.getFieldName());
        OrderType orderType = OrderType.fromType(orderTypeString);
        String addressIdByString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, ADDRESS_ID.getFieldName());
        Long addressId = addressIdByString != null ? Long.parseLong(addressIdByString) : null;
        String optionName = kakaoPaymentRedisRepository.getHashFieldValue(memberId, OPTION_NAME.getFieldName());

        Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayApproveParameters(memberId, orderId, transactionId, pgToken);

        ApproveResponseDTO approveResponseDTO = kakaoPaymentUtil.sendRequest("/approve", parameters, ApproveResponseDTO.class);

        ProductInventory product = productInventoryRepository.findProductByProductName(Objects.requireNonNull(approveResponseDTO).getItem_name());
        int quantity = approveResponseDTO.getQuantity();

        if (product.getStockQuantity() < quantity) {
            throw new KakaoPayException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다.");
        }

        int refundedPoints = product.calculatePointRefund(quantity);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        Point point = pointRepository.findByMember(member)
                .orElseGet(() -> {
                    Point newPoint = Point.builder()
                            .point(0)
                            .build();
                    return pointRepository.save(newPoint);
                });

        point.increasePoint(refundedPoints);

        product.decreaseQuantity(quantity);
        product.increasePurchaseParticipantCount(quantity);

        SinglePayment singlePayment = SinglePayment.builder().approveResponseDTO(approveResponseDTO).build();
        Amount amount = Amount.builder().approveResponseDTO(approveResponseDTO).build();
        singlePayment.addAmount(amount);

        if (Objects.requireNonNull(approveResponseDTO).getPayment_method_type().equals(PaymentMethod.CARD.name())) {
            CardInfo cardInfo = CardInfo.builder().approveResponseDTO(approveResponseDTO).build();
            singlePayment.addCardInfo(cardInfo);
        }

        saveOrderDetails(orderType, addressId, approveResponseDTO, optionName, memberId);
        singlePaymentRepository.save(singlePayment);

        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.SUCCESS.name());
    }

    public String checkPaymentStatus(Long memberId) {
        String status = kakaoPaymentRedisRepository.getPaymentStatus(memberId);
        if (status == null) {
            return PaymentStatus.NONE.name();
        }
        return status;
    }

    public void cancelPayment(Long memberId) {
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.CANCEL.name());
        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
    }

    public void failPayment(Long memberId) {
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.FAIL.name());
        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
    }

    public void updatePaymentStatus(Long memberId) {
        String status = kakaoPaymentRedisRepository.getPaymentStatus(memberId);
        String key = PAYMENT_STATUS.getFieldName() + memberId;
        if (status == null) {
            kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.NONE.name());
        } else if (status.equals(PaymentStatus.SUCCESS.name())) {
            kakaoPaymentRedisRepository.removePaymentStatus(key);
        }
    }

    private void saveOrderDetails(OrderType orderType, Long addressId, ApproveResponseDTO approveResponseDTO, String optionName, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        Product product = productInventoryRepository.findProductByProductName(approveResponseDTO.getItem_name());

        OrderDetails orderDetails = OrderDetails.builder()
                .member(member)
                .orderType(orderType)
                .approveResponseDTO(approveResponseDTO)
                .product(product)
                .optionName(optionName)
                .build();

        switch (orderType) {
            case PICKUP -> {
                String pickupPlace = kakaoPaymentRedisRepository.getHashFieldValue(memberId, PICKUP_PLACE.getFieldName());
                LocalDate pickupDate = LocalDate.parse(kakaoPaymentRedisRepository.getHashFieldValue(memberId, PICKUP_DATE.getFieldName()));
                LocalTime pickupTime = LocalTime.parse(kakaoPaymentRedisRepository.getHashFieldValue(memberId, PICKUP_TIME.getFieldName()));
                orderDetails.addOrderTypeByPickup(pickupPlace, pickupDate, pickupTime);
            }
            case DELIVER -> {
                Address address = addressRepository.findById(addressId)
                        .orElseThrow(() -> new KakaoPayException(ErrorCode.ADDRESS_NOT_FOUND, null));
                orderDetails.addOrderTypeByDeliver(address);
            }
        }

        orderDetailsRepository.save(orderDetails);
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
        fields.put(ORDER_ID.getFieldName(), partnerOrderId);
        fields.put(TRANSACTION_ID.getFieldName(), tid);
        fields.put(ORDER_TYPE.getFieldName(), orderType);
        fields.put(OPTION_NAME.getFieldName(), paymentRequestDTO.getOptionName());

        if (paymentRequestDTO instanceof DeliveryPaymentRequestDTO deliveryPaymentRequestDTO) {
            fields.put(ADDRESS_ID.getFieldName(), deliveryPaymentRequestDTO.getAddressId().toString());
        }

        if (paymentRequestDTO instanceof PickupPaymentRequestDTO pickupPaymentRequestDTO) {
            String pickupDate = pickupPaymentRequestDTO.getPickupDate().toString();
            String pickupTime = pickupPaymentRequestDTO.getPickupTime().toString();

            fields.put(PICKUP_PLACE.getFieldName(), pickupPaymentRequestDTO.getPickupPlace());
            fields.put(PICKUP_DATE.getFieldName(), pickupDate);
            fields.put(PICKUP_TIME.getFieldName(), pickupTime);
        }
        return fields;
    }

}
