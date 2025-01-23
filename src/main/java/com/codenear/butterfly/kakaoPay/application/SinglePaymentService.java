package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.HashMapUtil;
import com.codenear.butterfly.kakaoPay.domain.Amount;
import com.codenear.butterfly.kakaoPay.domain.CardInfo;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
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
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SinglePaymentService {

    @Value("${kakao.payment.cid}")
    private String CID;

    @Value("${kakao.payment.secret-key-dev}")
    private String secretKey;

    @Value("${kakao.payment.host}")
    private String host;

    @Value("${kakao.payment.request-url}")
    private String requestUrl;

    private final SinglePaymentRepository singlePaymentRepository;
    private final AddressRepository addressRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final MemberRepository memberRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final KakaoPaymentRedisRepository kakaoPaymentRedisRepository;
    private final PointRepository pointRepository;

    public ReadyResponseDTO kakaoPayReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType) {
        String partnerOrderId = UUID.randomUUID().toString();

        Map<String, Object> parameters = getKakaoPayReadyParameters(paymentRequestDTO, memberId, partnerOrderId);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        ReadyResponseDTO kakaoPayReady = new RestTemplate().postForObject(
                host + "/ready",
                requestEntity,
                ReadyResponseDTO.class);
        String tid = kakaoPayReady != null ? kakaoPayReady.getTid() : null;

        Map<String,String> fields = getKakaoPayReadyRedisFields(partnerOrderId,orderType, tid ,paymentRequestDTO);
        kakaoPaymentRedisRepository.addMultipleToHashSet(memberId,fields);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId,PaymentStatus.READY.name());

        return kakaoPayReady;
    }

    @Transactional
    public void approveResponse(String pgToken, Long memberId) {
        String orderId = kakaoPaymentRedisRepository.getHashFieldValue(memberId, "orderId");
        String transactionId = kakaoPaymentRedisRepository.getHashFieldValue(memberId, "transactionId");
        String orderTypeString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, "orderType");
        OrderType orderType = OrderType.fromType(orderTypeString);
        String addressIdByString = kakaoPaymentRedisRepository.getHashFieldValue(memberId, "addressId");
        Long addressId = addressIdByString != null ? Long.parseLong(addressIdByString) : null;
        String optionName = kakaoPaymentRedisRepository.getHashFieldValue(memberId, "optionName");

        Map<String, Object> parameters = getKakaoPayApproveParameters(memberId, orderId, transactionId, pgToken);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        ApproveResponseDTO approveResponseDTO = new RestTemplate().postForObject(
                host + "/approve",
                requestEntity,
                ApproveResponseDTO.class);

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

        SinglePayment singlePayment = createSinglePayment(approveResponseDTO);
        Amount amount = createAmount(approveResponseDTO);
        singlePayment.setAmount(amount);

        if (Objects.requireNonNull(approveResponseDTO).getPayment_method_type().equals("CARD")) {
            CardInfo cardInfo = createCardInfo(approveResponseDTO);
            singlePayment.setCardInfo(cardInfo);
        }

        saveOrderDetails(orderType, addressId, approveResponseDTO, optionName, memberId);
        singlePaymentRepository.save(singlePayment);

        kakaoPaymentRedisRepository.removeHashTableKey(memberId);
        kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.SUCCESS.name());
    }

    private void saveOrderDetails(OrderType orderType, Long addressId, ApproveResponseDTO approveResponseDTO, String optionName, Long memberId) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderType(orderType);
        orderDetails.setOrderCode(generateOrderCode());
        orderDetails.setCreatedAt(LocalDateTime.parse(approveResponseDTO.getCreated_at()));
        orderDetails.setTid(approveResponseDTO.getTid());

        if (OrderType.PICKUP.getType().equals(orderType.getType())) {
            orderDetails.setPickupPlace(kakaoPaymentRedisRepository.getHashFieldValue(memberId, "pickupPlace"));
            orderDetails.setPickupDate(LocalDate.parse(kakaoPaymentRedisRepository.getHashFieldValue(memberId, "pickupDate")));
            orderDetails.setPickupTime(LocalTime.parse(kakaoPaymentRedisRepository.getHashFieldValue(memberId, "pickupTime")));
        } else if (OrderType.DELIVER.getType().equals(orderType.getType())) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new KakaoPayException(ErrorCode.ADDRESS_NOT_FOUND, null));
            orderDetails.setAddress(address.getAddress());
            orderDetails.setDetailedAddress(address.getDetailedAddress());
        }

        orderDetails.setTotal(approveResponseDTO.getAmount().getTotal());
        orderDetails.setProductName(approveResponseDTO.getItem_name());

        Product product = productInventoryRepository.findProductByProductName(approveResponseDTO.getItem_name());

        orderDetails.setProductImage(product.getProductImage());
        orderDetails.setOptionName(optionName);
        orderDetails.setQuantity(approveResponseDTO.getQuantity());
        orderDetails.setOrderStatus(OrderStatus.READY);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage()));
        orderDetails.setMember(member);

        orderDetailsRepository.save(orderDetails);
    }

    private String generateOrderCode() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSSS");
        return now.format(formatter);
    }

    private SinglePayment createSinglePayment(ApproveResponseDTO approveResponseDTO) {
        SinglePayment singlePayment = new SinglePayment();
        singlePayment.setAid(Objects.requireNonNull(approveResponseDTO).getAid());
        singlePayment.setTid(approveResponseDTO.getTid());
        singlePayment.setCid(approveResponseDTO.getCid());
        singlePayment.setSid(approveResponseDTO.getSid());
        singlePayment.setPartnerOrderId(approveResponseDTO.getPartner_order_id());
        singlePayment.setPartnerUserId(approveResponseDTO.getPartner_user_id());
        singlePayment.setPaymentMethodType(approveResponseDTO.getPayment_method_type());
        singlePayment.setItemName(approveResponseDTO.getItem_name());
        singlePayment.setItemCode(approveResponseDTO.getItem_code());
        singlePayment.setQuantity(approveResponseDTO.getQuantity());
        singlePayment.setCreatedAt(approveResponseDTO.getCreated_at());
        singlePayment.setApprovedAt(approveResponseDTO.getApproved_at());
        singlePayment.setPayload(approveResponseDTO.getPayload());
        return singlePayment;
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
        String key = "paymentStatus:" + memberId;
        if (status == null) {
            kakaoPaymentRedisRepository.savePaymentStatus(memberId, PaymentStatus.NONE.name());
        } else if (status.equals(PaymentStatus.SUCCESS.name())) {
            kakaoPaymentRedisRepository.removePaymentStatus(key);
        }
    }

    private Amount createAmount(ApproveResponseDTO approveResponseDTO) {
        Amount amount = new Amount();
        amount.setTotal(Objects.requireNonNull(approveResponseDTO).getAmount().getTotal());
        amount.setTaxFree(approveResponseDTO.getAmount().getTax_free());
        amount.setVat(approveResponseDTO.getAmount().getVat());
        amount.setPoint(approveResponseDTO.getAmount().getPoint());
        amount.setDiscount(approveResponseDTO.getAmount().getDiscount());
        return amount;
    }

    private CardInfo createCardInfo(ApproveResponseDTO approveResponseDTO) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setApprovedId(approveResponseDTO.getCard_info().getApproved_id());
        cardInfo.setBin(approveResponseDTO.getCard_info().getBin());
        cardInfo.setCardMid(approveResponseDTO.getCard_info().getCard_mid());
        cardInfo.setCardType(approveResponseDTO.getCard_info().getCard_type());
        cardInfo.setInstallMonth(approveResponseDTO.getCard_info().getInstall_month());
        cardInfo.setCardItemCode(approveResponseDTO.getCard_info().getCard_item_code());
        cardInfo.setInterestFreeInstall(approveResponseDTO.getCard_info().getInterest_free_install());
        cardInfo.setKakaopayPurchaseCorp(approveResponseDTO.getCard_info().getKakaopay_purchase_corp());
        cardInfo.setKakaopayPurchaseCorpCode(approveResponseDTO.getCard_info().getKakaopay_purchase_corp_code());
        cardInfo.setKakaopayIssuerCorp(approveResponseDTO.getCard_info().getKakaopay_issuer_corp());
        cardInfo.setKakaopayIssuerCorpCode(approveResponseDTO.getCard_info().getKakaopay_issuer_corp_code());
        return cardInfo;
    }

    private Map<String,String> getKakaoPayReadyRedisFields(
            final String partnerOrderId,
            final String orderType,
            final String tid,
            final BasePaymentRequestDTO paymentRequestDTO) {

        Map<String, String> fields = new HashMapUtil<>();
        fields.put("orderId", partnerOrderId);
        fields.put("transactionId", tid);
        fields.put("orderType", orderType);
        fields.put("optionName", paymentRequestDTO.getOptionName());

        if(paymentRequestDTO instanceof DeliveryPaymentRequestDTO deliveryPaymentRequestDTO) {
            fields.put("addressId", deliveryPaymentRequestDTO.getAddressId().toString());
        }

        if (paymentRequestDTO instanceof PickupPaymentRequestDTO pickupPaymentRequestDTO) {
            String pickupDate = pickupPaymentRequestDTO.getPickupDate().toString();
            String pickupTime = pickupPaymentRequestDTO.getPickupTime().toString();

            fields.put("pickupPlace",pickupPaymentRequestDTO.getPickupPlace());
            fields.put("pickupDate",pickupDate);
            fields.put("pickupTime",pickupTime);
        }
        return fields;
    }


    private Map<String, Object> getKakaoPayReadyParameters(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String partnerOrderId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("partner_order_id", partnerOrderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("item_name", paymentRequestDTO.getProductName());
        parameters.put("quantity", paymentRequestDTO.getQuantity());
        parameters.put("total_amount", paymentRequestDTO.getTotal());
        parameters.put("vat_amount", 0);
        parameters.put("tax_free_amount", 0);
        parameters.put("approval_url", requestUrl + "/payment/success?memberId=" + memberId);
        parameters.put("cancel_url", requestUrl + "/payment/cancel?memberId=" + memberId);
        parameters.put("fail_url", requestUrl + "/payment/fail?memberId=" + memberId);
        parameters.put("return_custom_url", "butterfly://");
        return parameters;
    }

    private Map<String, Object> getKakaoPayApproveParameters(Long memberId, String orderId, String transactionId, String pgToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", transactionId);
        parameters.put("partner_order_id", orderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        return headers;
    }
}
