package com.codenear.butterfly.payment.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.HashMapUtil;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.Amount;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.PaymentRedisField;
import com.codenear.butterfly.payment.domain.SinglePayment;
import com.codenear.butterfly.payment.domain.dto.OrderType;
import com.codenear.butterfly.payment.domain.dto.PaymentStatus;
import com.codenear.butterfly.payment.domain.dto.handler.ApproveFreePaymentHandler;
import com.codenear.butterfly.payment.domain.dto.handler.ApproveHandler;
import com.codenear.butterfly.payment.domain.dto.order.OrderDTO;
import com.codenear.butterfly.payment.domain.dto.rabbitmq.InventoryDecreaseMessageDTO;
import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.exception.PaymentException;
import com.codenear.butterfly.payment.kakaoPay.domain.dto.ApproveResponseDTO;
import com.codenear.butterfly.payment.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SinglePaymentRepository singlePaymentRepository;
    private final AddressRepository addressRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final MemberRepository memberRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final PaymentRedisRepository paymentRedisRepository;
    private final PointRepository pointRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final FCMFacade fcmFacade;

    public String checkPaymentStatus(Long memberId) {
        String status = paymentRedisRepository.getPaymentStatus(memberId);
        if (status == null) {
            return PaymentStatus.NONE.name();
        }
        return status;
    }

    public void updatePaymentStatus(Long memberId) {
        String status = paymentRedisRepository.getPaymentStatus(memberId);
        String key = PaymentRedisField.PAYMENT_STATUS.getFieldName() + memberId;
        if (status == null) {
            paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.NONE.name());
        } else if (status.equals(PaymentStatus.SUCCESS.name())) {
            paymentRedisRepository.removePaymentStatus(key);
        }
    }

    /**
     * 주문 가능 여부 확인
     *
     * @param orderDTO 주문한 상품명과 상품개수를 담은 DTO
     */
    public void isPossibleToOrder(OrderDTO orderDTO) {
        String remainderProductQuantityStr = paymentRedisRepository.getRemainderProductQuantity(orderDTO.productName());
        int remainderProductQuantity;

        if (remainderProductQuantityStr == null) {
            ProductInventory product = productInventoryRepository.findProductByProductName(orderDTO.productName());

            if (product == null) {
                throw new PaymentException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다.");
            }

            remainderProductQuantity = product.getStockQuantity();
            paymentRedisRepository.saveStockQuantity(orderDTO.productName(), remainderProductQuantity);
        } else {
            remainderProductQuantity = Integer.parseInt(remainderProductQuantityStr);
        }

        if (remainderProductQuantity < orderDTO.orderQuantity()) {
            throw new PaymentException(ErrorCode.INSUFFICIENT_STOCK, "재고가 부족합니다.");
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
        paymentRedisRepository.restoreStockOnOrderCancellation(productName, quantity);
        paymentRedisRepository.removeReserveProduct(productName, quantity, orderId);
    }

    /**
     * redis에 저장된 String 데이터 타입의 값을 int로 형변환 하여 반환한다.
     *
     * @param memberId 사용자 아이디
     * @param key      redis에서 가져올 키
     * @return int형 데이터
     */
    protected int parsingStringToInt(Long memberId, String key) {
        String keyString = paymentRedisRepository.getHashFieldValue(memberId, key);

        return keyString == null ? 0 : Integer.parseInt(keyString);
    }

    /**
     * redis에 저장된 String 데이터 타입의 값을 Long으로 형변환 하여 반환한다.
     *
     * @param memberId 사용자 아이디
     * @param key      redis에서 가져올 키
     * @return Long형 데이터
     */
    protected Long parsingStringToLong(Long memberId, String key) {
        String keyString = paymentRedisRepository.getHashFieldValue(memberId, key);

        return keyString == null ? null : Long.parseLong(keyString);
    }

    /**
     * 결제금액이 0원일 때 처리
     *
     * @param memberId          사용자 아이디
     * @param paymentRequestDTO 결제 요청 DTO
     * @param orderId           결제 UUID
     */
    protected void approveFreeResponse(Long memberId, BasePaymentRequestDTO paymentRequestDTO, String orderId) {
        String orderTypeString = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.ORDER_TYPE.getFieldName());
        OrderType orderType = OrderType.fromType(orderTypeString);
        Long addressId = parsingStringToLong(memberId, PaymentRedisField.ADDRESS_ID.getFieldName());
        String optionName = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.OPTION_NAME.getFieldName());

        ProductInventory product = productInventoryRepository.findProductByProductName(paymentRequestDTO.getProductName());

        processPaymentSuccess(memberId, orderType, addressId, optionName, product,
                new ApproveFreePaymentHandler(paymentRequestDTO, orderId));
        fcmFacade.sendMessage(NotifyMessage.ORDER_SUCCESS, memberId);
    }

    /**
     * 멤버 가져오기
     *
     * @param memberId 사용자 아이디
     * @return 멤버 객체
     */
    protected Member loadByMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, null));
    }

    protected void validateRemainingPointForPurchase(Member member, int remainPoint) {
        if (remainPoint > member.getPoint().getPoint()) {
            throw new PaymentException(ErrorCode.INVALID_POINT_VALUE, "포인트가 부족합니다.");
        }
    }

    protected void decreaseUsePoint(Long memberId, int usePoint) {
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

    /**
     * 결제 (카카오페이 or 자체결제)
     *
     * @param memberId   사용자 아이디
     * @param orderType  주문 타입 (PICKUP, DELIVERY)
     * @param addressId  배송지 아이디
     * @param optionName 선택한 상품 옵션명
     * @param product    상품 정보
     * @param handler    결제 응답 객체 (ApproveResponseDTO 또는 BasePaymentRequestDTO)
     */
    protected void processPaymentSuccess(Long memberId,
                                         OrderType orderType,
                                         Long addressId,
                                         String optionName,
                                         ProductInventory product,
                                         ApproveHandler handler) {

        SinglePayment singlePayment = handler.createSinglePayment(memberId);
        Amount amount = handler.createAmount();

        singlePayment.addAmount(amount);

        handler.createCardInfo().ifPresent(singlePayment::addCardInfo);

        int usePoint = handler.getPoint();
        decreaseUsePoint(memberId, usePoint);

        saveOrderDetails(orderType, addressId, handler.getOrderDetailDto(), optionName, memberId, usePoint, product);

        singlePaymentRepository.save(singlePayment);

        paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.SUCCESS.name());
        paymentRedisRepository.removeReserveProduct(handler.getProductName(), handler.getQuantity(), handler.getOrderId());
        paymentRedisRepository.removeHashTableKey(memberId);

        // DB 재고 업데이트를 위해 RabbitMQ 메시지 전송
        InventoryDecreaseMessageDTO message = new InventoryDecreaseMessageDTO(handler.getProductName(), handler.getQuantity());
        applicationEventPublisher.publishEvent(message);
    }

    /**
     * 결제 준비 단계에서 Redis에 저장할 필드를 생성
     *
     * @param partnerOrderId    파트너사 주문 ID
     * @param orderType         주문 타입
     * @param tid               카카오페이 트랜잭션 ID
     * @param paymentRequestDTO 결제 요청 정보를 담고 있는 객체 (BasePaymentRequestDTO 타입)
     * @return Redis에 저장할 필드 값들을 키-값 쌍으로 담고 있는 Map 객체
     */

    protected Map<String, String> getPayReadyRedisFields(
            final String partnerOrderId,
            final String orderType,
            final String tid,
            final BasePaymentRequestDTO paymentRequestDTO) {

        Map<String, String> fields = new HashMapUtil<>();
        fields.put(PaymentRedisField.ORDER_ID.getFieldName(), partnerOrderId);
        fields.put(PaymentRedisField.TRANSACTION_ID.getFieldName(), tid);
        fields.put(PaymentRedisField.ORDER_TYPE.getFieldName(), orderType);
        fields.put(PaymentRedisField.OPTION_NAME.getFieldName(), paymentRequestDTO.getOptionName());
        fields.put(PaymentRedisField.POINT.getFieldName(), String.valueOf(paymentRequestDTO.getPoint()));

        if (paymentRequestDTO instanceof DeliveryPaymentRequestDTO deliveryPaymentRequestDTO) {
            fields.put(PaymentRedisField.ADDRESS_ID.getFieldName(), deliveryPaymentRequestDTO.getAddressId().toString());
            fields.put(PaymentRedisField.DELIVER_DATE.getFieldName(), deliveryPaymentRequestDTO.deliverDateFormat());
        }

        if (paymentRequestDTO instanceof PickupPaymentRequestDTO pickupPaymentRequestDTO) {
            String pickupDate = pickupPaymentRequestDTO.getPickupDate().toString();
            String pickupTime = pickupPaymentRequestDTO.getPickupTime().toString();

            fields.put(PaymentRedisField.PICKUP_PLACE.getFieldName(), pickupPaymentRequestDTO.getPickupPlace());
            fields.put(PaymentRedisField.PICKUP_DATE.getFieldName(), pickupDate);
            fields.put(PaymentRedisField.PICKUP_TIME.getFieldName(), pickupTime);
        }
        return fields;
    }

    /**
     * 주문 상세 정보를 생성하고 저장한다.
     *
     * @param orderType   주문 타입 (PICKUP, DELIVERY)
     * @param addressId   배송지 ID
     * @param responseDTO 결제 응답 객체 (ApproveResponseDTO 또는 BasePaymentRequestDTO)
     * @param optionName  상품 옵션명
     * @param memberId    사용자 아이디
     * @param point       사용 포인트
     * @param product     상품 정보
     */

    private <T> void saveOrderDetails(OrderType orderType,
                                      Long addressId,
                                      T responseDTO,
                                      String optionName,
                                      Long memberId,
                                      int point,
                                      Product product) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));

        OrderDetails orderDetails = createOrderDetails(orderType, responseDTO, member, product, optionName, point);
        addOrderTypeDetails(orderDetails, orderType, memberId, addressId);

        orderDetailsRepository.save(orderDetails);
    }

    /**
     * 주문 상세 정보를 생성
     * - 승인 결제(ApproveResponseDTO) 또는 무료 결제(BasePaymentRequestDTO)에 따라 다른 빌더를 사용하여 {@link OrderDetails} 객체 생성
     *
     * @param orderType   주문 유형 (PICKUP / DELIVER)
     * @param responseDTO 결제 응답 객체 (ApproveResponseDTO 또는 BasePaymentRequestDTO)
     * @param member      사용자
     * @param product     상품 정보
     * @param optionName  선택한 상품 옵션명
     * @param point       사용한 포인트 금액
     * @return 생성된 {@link OrderDetails} 객체
     * @throws IllegalArgumentException 지원하지 않는 DTO 타입일 경우 발생합니다.
     */
    private OrderDetails createOrderDetails(OrderType orderType,
                                            Object responseDTO,
                                            Member member,
                                            Product product,
                                            String optionName,
                                            int point) {

        if (responseDTO instanceof ApproveResponseDTO approveResponseDTO) {
            return createApproveOrderDetails(orderType, approveResponseDTO, member, product, optionName, point);
        }

        if (responseDTO instanceof ConfirmResponseDTO confirmResponseDTO) {
            return createTossOrderDetails(orderType, confirmResponseDTO, member, product, optionName, point);
        }

        if (responseDTO instanceof BasePaymentRequestDTO basePaymentRequestDTO) {
            return createFreeOrderDetails(orderType, basePaymentRequestDTO, member, product);
        }

        throw new PaymentException(ErrorCode.INVALID_APPROVE_DATA_TYPE, "지원하지 않는 주문 정보 타입 입니다.");
    }

    /**
     * 승인 결제(ApproveResponseDTO) 주문 상세 생성 - KAKAO
     */
    private OrderDetails createApproveOrderDetails(OrderType orderType,
                                                   ApproveResponseDTO dto,
                                                   Member member,
                                                   Product product,
                                                   String optionName,
                                                   int point) {

        return OrderDetails.kakaoPaymentBuilder()
                .member(member)
                .orderType(orderType)
                .approveResponseDTO(dto)
                .product(product)
                .optionName(optionName)
                .point(point)
                .buildKakaoPayment();
    }

    /**
     * 승인 결제(confirmResponseDTO) 주문 상세 생성 - TOSS
     */

    private OrderDetails createTossOrderDetails(OrderType orderType,
                                                ConfirmResponseDTO dto,
                                                Member member,
                                                Product product,
                                                String optionName,
                                                int point) {
        return OrderDetails.tossPaymentBuilder()
                .member(member)
                .orderType(orderType)
                .confirmResponseDTO(dto)
                .product(product)
                .optionName(optionName)
                .point(point)
                .buildTossPayment();
    }

    /**
     * 무료 결제(BasePaymentRequestDTO) 주문 상세 생성
     */
    private OrderDetails createFreeOrderDetails(OrderType orderType,
                                                BasePaymentRequestDTO dto,
                                                Member member,
                                                Product product) {

        return OrderDetails.freeOrderBuilder()
                .member(member)
                .orderType(orderType)
                .basePaymentRequestDTO(dto)
                .product(product)
                .buildFreeOrder();
    }

    /**
     * 주문 유형에 따라 {@link OrderDetails} 객체에 추가 정보 주입
     * - PICKUP : 픽업 장소, 날짜, 시간을 Redis에서 조회하여 설정
     * - DELIVER : 배송 주소와 배송 날짜를 조회하여 설정
     *
     * @param orderDetails 주문 상세 정보 객체
     * @param orderType    주문 타입 (PICKUP / DELIVER)
     * @param memberId     사용자 아이디
     * @param addressId    배송지 아이디
     * @throws PaymentException 주소 정보가 존재하지 않거나, 잘못된 경우 예외 발생
     */
    private void addOrderTypeDetails(OrderDetails orderDetails,
                                     OrderType orderType,
                                     Long memberId,
                                     Long addressId) {

        switch (orderType) {
            case PICKUP -> {
                String pickupPlace = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.PICKUP_PLACE.getFieldName());
                LocalDate pickupDate = LocalDate.parse(paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.PICKUP_DATE.getFieldName()));
                LocalTime pickupTime = LocalTime.parse(paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.PICKUP_TIME.getFieldName()));

                orderDetails.addOrderTypeByPickup(pickupPlace, pickupDate, pickupTime);
            }
            case DELIVER -> {
                Address address = addressRepository.findById(addressId)
                        .orElseThrow(() -> new PaymentException(ErrorCode.ADDRESS_NOT_FOUND, null));
                LocalDate deliverDate = LocalDate.parse(paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.DELIVER_DATE.getFieldName()));

                orderDetails.addOrderTypeByDeliver(address, deliverDate);
            }
        }
    }
}
