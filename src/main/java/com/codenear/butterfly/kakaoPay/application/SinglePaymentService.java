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
import com.codenear.butterfly.kakaoPay.domain.dto.order.OrderDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.rabbitmq.InventoryDecreaseMessageDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.KakaoPaymentRedisRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.kakaoPay.exception.KakaoPayException;
import com.codenear.butterfly.kakaoPay.util.KakaoPayRabbitMQProducer;
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
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.DELIVER_DATE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.OPTION_NAME;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.ORDER_ID;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.ORDER_TYPE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PAYMENT_STATUS;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_DATE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_PLACE;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.PICKUP_TIME;
import static com.codenear.butterfly.kakaoPay.domain.KakaoPayRedisField.POINT;
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
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;
    private final KakaoPayRabbitMQProducer rabbitMQProducer;
    private final PointRepository pointRepository;

    public ReadyResponseDTO kakaoPayReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType) {
        String partnerOrderId = UUID.randomUUID().toString();

        // 재고 예약
        kakaoPaymentRedisRepository.reserveStock(paymentRequestDTO.getProductName(), paymentRequestDTO.getQuantity(), partnerOrderId);

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

        SinglePayment singlePayment = SinglePayment.builder().approveResponseDTO(approveResponseDTO).build();
        Amount amount = Amount.builder().approveResponseDTO(approveResponseDTO).build();
        singlePayment.addAmount(amount);

        if (Objects.requireNonNull(approveResponseDTO).getPayment_method_type().equals(PaymentMethod.CARD.name())) {
            CardInfo cardInfo = CardInfo.builder().approveResponseDTO(approveResponseDTO).build();
            singlePayment.addCardInfo(cardInfo);
        }

        int usePoint = parsingStringToInt(memberId, POINT.getFieldName());
        decreaseUsePoint(memberId, usePoint);
        saveOrderDetails(orderType, addressId, approveResponseDTO, optionName, memberId, usePoint);

        singlePaymentRepository.save(singlePayment);

        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.SUCCESS.name());
        kakaoPaymentRedisRepository.removeReserveProduct(approveResponseDTO.getItem_name(), quantity, orderId);

        // DB 재고 업데이트를 위해 RabbitMQ 메시지 전송
        InventoryDecreaseMessageDTO message = new InventoryDecreaseMessageDTO(product.getProductName(), approveResponseDTO.getQuantity());
        rabbitMQProducer.sendMessage(message);
    }

    public String checkPaymentStatus(Long memberId) {
        String status = kakaoPaymentRedisRepository.getPaymentStatus(memberId);
        if (status == null) {
            return PaymentStatus.NONE.name();
        }
        return status;
    }

    public void cancelPayment(Long memberId, String productName, int quantity) {
        restoreQuantity(productName, quantity, kakaoPaymentRedisRepository.getHashFieldValue(memberId, ORDER_ID.getFieldName()));
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.CANCEL.name());
        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
    }

    public void failPayment(Long memberId, String productName, int quantity) {
        restoreQuantity(productName, quantity, kakaoPaymentRedisRepository.getHashFieldValue(memberId, ORDER_ID.getFieldName()));
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

    /**
     * 주문 가능 여부 확인
     *
     * @param orderDTO 주문한 상품명과 상품개수를 담은 DTO
     */
    public void isPossibleToOrder(OrderDTO orderDTO) {
        String remainderProductQuantityStr = kakaoPaymentRedisRepository.getRemainderProductQuantity(orderDTO.productName());
        int remainderProductQuantity;

        if (remainderProductQuantityStr == null) {
            ProductInventory product = productInventoryRepository.findProductByProductName(orderDTO.productName());

            if (product == null) {
                throw new KakaoPayException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다.");
            }

            remainderProductQuantity = product.getStockQuantity();
            kakaoPaymentRedisRepository.saveStockQuantity(orderDTO.productName(), remainderProductQuantity);
        } else {
            remainderProductQuantity = Integer.parseInt(remainderProductQuantityStr);
        }

        if (remainderProductQuantity < orderDTO.orderQuantity()) {
            throw new KakaoPayException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다.");
        }
    }

    /**
     * 예약된 재고 반환 후 key 삭제
     *
     * @param productName 상품 이름
     * @param quantity    예약 개수
     * @param orderId     주문 id
     */
    public void restoreQuantity(String productName, int quantity, String orderId) {
        kakaoPaymentRedisRepository.restoreStockOnOrderCancellation(productName, quantity);
        kakaoPaymentRedisRepository.removeReserveProduct(productName, quantity, orderId);
    }

    private void saveOrderDetails(OrderType orderType, Long addressId, ApproveResponseDTO approveResponseDTO, String optionName, Long memberId, int point) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        Product product = productInventoryRepository.findProductByProductName(approveResponseDTO.getItem_name());

        OrderDetails orderDetails = OrderDetails.builder()
                .member(member)
                .orderType(orderType)
                .approveResponseDTO(approveResponseDTO)
                .product(product)
                .optionName(optionName)
                .point(point)
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
                LocalDate deliverDate = LocalDate.parse(kakaoPaymentRedisRepository.getHashFieldValue(memberId, DELIVER_DATE.getFieldName()));
                orderDetails.addOrderTypeByDeliver(address, deliverDate);
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
        fields.put(POINT.getFieldName(), String.valueOf(paymentRequestDTO.getPoint()));

        if (paymentRequestDTO instanceof DeliveryPaymentRequestDTO deliveryPaymentRequestDTO) {
            fields.put(ADDRESS_ID.getFieldName(), deliveryPaymentRequestDTO.getAddressId().toString());
            fields.put(DELIVER_DATE.getFieldName(), deliveryPaymentRequestDTO.deliverDateFormat());
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

    /**
     * redis에 저장된 String 데이터 타입의 값을 int로 형변환 하여 반환한다.
     *
     * @param memberId 사용자 아이디
     * @param key      redis에서 가져올 키
     * @return int형 데이터
     */
    private int parsingStringToInt(Long memberId, String key) {
        String keyString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, key);

        return keyString == null ? 0 : Integer.parseInt(keyString);
    }

    private void decreaseUsePoint(Long memberId, int usePoint) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        Point point = pointRepository.findByMember(member)
                .orElseGet(() -> {
                    Point newPoint = Point.createPoint()
                            .member(member)
                            .build();
                    return pointRepository.save(newPoint);
                });

        point.decreasePoint(usePoint);
    }
}
