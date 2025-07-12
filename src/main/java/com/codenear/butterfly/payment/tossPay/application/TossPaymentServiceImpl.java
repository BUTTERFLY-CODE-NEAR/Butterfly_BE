package com.codenear.butterfly.payment.tossPay.application;

import com.codenear.butterfly.address.domain.repository.AddressRepository;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.application.PaymentCancel;
import com.codenear.butterfly.payment.application.PaymentService;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.PaymentRedisField;
import com.codenear.butterfly.payment.domain.dto.OrderType;
import com.codenear.butterfly.payment.domain.dto.PaymentStatus;
import com.codenear.butterfly.payment.domain.dto.handler.ApprovePaymentHandler;
import com.codenear.butterfly.payment.domain.dto.handler.CancelPaymentHandler;
import com.codenear.butterfly.payment.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.payment.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.payment.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.payment.exception.PaymentException;
import com.codenear.butterfly.payment.tossPay.domain.dto.CancelResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ConfirmResponseDTO;
import com.codenear.butterfly.payment.tossPay.domain.dto.ReadyResponseDTO;
import com.codenear.butterfly.payment.tossPay.util.TossPaymentUtil;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class TossPaymentServiceImpl extends PaymentService implements TossPaymentService, PaymentCancel {
    private final PaymentRedisRepository paymentRedisRepository;
    private final TossPaymentUtil<Object> tossPaymentUtil;
    private final ProductInventoryRepository productInventoryRepository;

    public TossPaymentServiceImpl(SinglePaymentRepository singlePaymentRepository,
                                  AddressRepository addressRepository,
                                  OrderDetailsRepository orderDetailsRepository,
                                  MemberRepository memberRepository,
                                  ProductInventoryRepository productInventoryRepository,
                                  PaymentRedisRepository kakaoPaymentRedisRepository,
                                  PointRepository pointRepository,
                                  ApplicationEventPublisher applicationEventPublisher,
                                  FCMFacade fcmFacade,
                                  TossPaymentUtil<Object> tossPaymentUtil,
                                  CancelPaymentRepository cancelPaymentRepository) {
        super(singlePaymentRepository, addressRepository, orderDetailsRepository, memberRepository, productInventoryRepository, kakaoPaymentRedisRepository, pointRepository, applicationEventPublisher, fcmFacade, cancelPaymentRepository);
        this.paymentRedisRepository = kakaoPaymentRedisRepository;
        this.tossPaymentUtil = tossPaymentUtil;
        this.productInventoryRepository = productInventoryRepository;
    }

    /**
     * Confirm전 재고 예약 및 검증 데이터 저장
     * 결제값이 0원이면 검증 데이터(를 저장하지 않는다.
     *
     * @param memberId              사용자 아이디
     * @param basePaymentRequestDTO 결제 데이터
     * @return ReadyResponseDTO
     */
    @Override
    public ReadyResponseDTO paymentReady(BasePaymentRequestDTO basePaymentRequestDTO, Long memberId, String orderType) {
        Member member = super.loadByMember(memberId);
        super.validateRemainingPointForPurchase(member, basePaymentRequestDTO.getPoint());
        String orderId = UUID.randomUUID().toString();
        paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.READY.name());
        paymentRedisRepository.reserveStock(basePaymentRequestDTO.getProductName(), basePaymentRequestDTO.getQuantity(), orderId);

        Map<String, String> fields = super.getPayReadyRedisFields(orderId, orderType, null, basePaymentRequestDTO);
        paymentRedisRepository.addMultipleToHashSet(memberId, fields);
        paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.READY.name());

        if (basePaymentRequestDTO.getTotal() == 0) {
            super.approveFreeResponse(memberId, basePaymentRequestDTO, orderId);
            return null;
        }

        Map<String, String> parameters = tossPaymentUtil.preConfirmParameter(basePaymentRequestDTO.getQuantity(), basePaymentRequestDTO.getTotal());
        paymentRedisRepository.addMultipleToHashSet(memberId, parameters);

        return ReadyResponseDTO.builder()
                .memberId(memberId)
                .orderId(orderId)
                .build();
    }

    @Override
    public void confirm(Long memberId, String paymentKey, String orderId, int amount) {
        verifyPayment(memberId, amount, orderId);

        String orderTypeString = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.ORDER_TYPE.getFieldName());
        OrderType orderType = OrderType.fromType(orderTypeString);
        Long addressId = super.parsingStringToLong(memberId, PaymentRedisField.ADDRESS_ID.getFieldName());
        String optionName = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.OPTION_NAME.getFieldName());

        Map<String, Object> parameters = tossPaymentUtil.confirmParameter(paymentKey, orderId, amount);

        ConfirmResponseDTO confirmResponse = tossPaymentUtil.sendRequest("/confirm", parameters, ConfirmResponseDTO.class);
        confirmResponse.setQuantity(this.parsingStringToInt(memberId, PaymentRedisField.QUANTITY.getFieldName()));
        paymentRedisRepository.addHashSetField(memberId, PaymentRedisField.TRANSACTION_ID.getFieldName(), confirmResponse.getPaymentKey());

        ProductInventory product = productInventoryRepository.findProductByProductName(confirmResponse.getOrderName());

        int usePoint = super.parsingStringToInt(memberId, PaymentRedisField.POINT.getFieldName());
        super.processPaymentSuccess(memberId, orderType, addressId, optionName, product, new ApprovePaymentHandler<ConfirmResponseDTO>(confirmResponse, usePoint));
    }

    @Override
    public void cancel(CancelRequestDTO cancelRequestDTO, OrderDetails orderDetails) {

        Map<String, Object> parameters = tossPaymentUtil.cancelParameter(cancelRequestDTO.getCancelReason());
        CancelResponseDTO cancelResponseDTO = tossPaymentUtil.sendRequest("/" + orderDetails.getTid() + "/cancel", parameters, CancelResponseDTO.class);
        cancelResponseDTO.setQuantity(orderDetails.getQuantity());

        super.processPaymentCancel(new CancelPaymentHandler<CancelResponseDTO>(cancelResponseDTO, orderDetails), orderDetails.getMember().getId());
    }

    @Override
    public String getProvider() {
        return "TOSS";
    }

    @Override
    public void failPayment(Long memberId, String productName, int quantity) {
        super.restoreQuantity(productName, quantity, paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.ORDER_ID.getFieldName()));
        paymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.FAIL.name());
        paymentRedisRepository.removeHashTableKey(memberId);
    }

    /**
     * 토스 결제 승인 API 요청을 보내기 전, 사용자 주문 금액과 주문 번호가 서버가 가지고 있는 값과 일치하는지 확인
     *
     * @param memberId 사용자 아이디
     * @param amount   가격
     * @param orderId  주문 아이디
     */
    private void verifyPayment(Long memberId, int amount, String orderId) {
        int storeAmount = super.parsingStringToInt(memberId, PaymentRedisField.TOTAL_AMOUNT.getFieldName());
        String storeOrderId = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.ORDER_ID.getFieldName());
        if (!storeOrderId.equals(orderId) && storeAmount != amount) {
            String productName = paymentRedisRepository.getHashFieldValue(memberId, PaymentRedisField.PRODUCT_NAME.getFieldName());
            int quantity = this.parsingStringToInt(memberId, PaymentRedisField.QUANTITY.getFieldName());
            failPayment(memberId, productName, quantity);
            throw new PaymentException(ErrorCode.PAY_FAILED, "유효한 주문이 아닙니다. 다시 확인해주세요.");
        }
    }
}
