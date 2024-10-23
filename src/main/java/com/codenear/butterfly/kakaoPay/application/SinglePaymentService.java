package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.kakaoPay.domain.Amount;
import com.codenear.butterfly.kakaoPay.domain.CardInfo;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.SinglePayment;
import com.codenear.butterfly.kakaoPay.domain.dto.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderType;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ReadyResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.SinglePaymentRepository;
import com.codenear.butterfly.kakaoPay.exception.KakaoPayException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, String> redisTemplate;

    public ReadyResponseDTO kakaoPayReady(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String orderType) {
        String partnerOrderId = UUID.randomUUID().toString();

        // 카카오페이 요청 양식
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("partner_order_id", partnerOrderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("item_name", paymentRequestDTO.getProductName());
        parameters.put("quantity", paymentRequestDTO.getQuantity());
        parameters.put("total_amount", paymentRequestDTO.getTotal());
        parameters.put("vat_amount", 0);
        parameters.put("tax_free_amount", 0);
        parameters.put("approval_url", requestUrl+"/payment/success");
        parameters.put("cancel_url", requestUrl+"/payment/cancel");
        parameters.put("fail_url", requestUrl+"/payment/fail");

        // 파라미터, 헤더
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        ReadyResponseDTO kakaoPayReady = restTemplate.postForObject(
                host+"/ready",
                requestEntity,
                ReadyResponseDTO.class);

        // Redis에 주문 ID와 거래 ID 저장
        saveOrderId(memberId, partnerOrderId);
        saveTransactionId(memberId, Objects.requireNonNull(kakaoPayReady).getTid());

        saveOrderTypeAndAddressId(memberId, orderType, paymentRequestDTO);
        return kakaoPayReady;
    }

    private void saveOrderTypeAndAddressId(Long memberId, String orderType, BasePaymentRequestDTO paymentRequestDTO) {
        String orderTypeKey = "orderType:" + memberId;
        redisTemplate.opsForValue().set(orderTypeKey, orderType, 30, TimeUnit.MINUTES);

        if ("deliver".equals(orderType)) {
            DeliveryPaymentRequestDTO deliveryDTO = (DeliveryPaymentRequestDTO) paymentRequestDTO;
            String addressIdKey = "addressId:" + memberId;
            redisTemplate.opsForValue().set(addressIdKey, deliveryDTO.getAddressId().toString(), 30, TimeUnit.MINUTES);
        }
    }

    @Transactional
    public void approveResponse(String pgToken, Long memberId) {
        String orderId = getOrderId(memberId);
        String transactionId = getTransactionId(memberId);
        String orderTypeString = getOrderType(memberId);
        OrderType orderType = OrderType.fromType(orderTypeString);
        Long addressId = getAddressId(memberId);

        // 카카오 요청
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", transactionId);
        parameters.put("partner_order_id", orderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        ApproveResponseDTO approveResponseDTO = restTemplate.postForObject(
                host+"/approve",
                requestEntity,
                ApproveResponseDTO.class);

        // 결제 데이터 저장
        SinglePayment singlePayment = createSinglePayment(approveResponseDTO);

        Amount amount = createAmount(approveResponseDTO);
        singlePayment.setAmount(amount);

        // CardInfo 엔티티 생성 및 설정
        if (Objects.requireNonNull(approveResponseDTO).getPayment_method_type().equals("CARD")) {
            CardInfo cardInfo = createCardInfo(approveResponseDTO);
            singlePayment.setCardInfo(cardInfo);
        }

        saveOrderDetails(orderType, addressId, approveResponseDTO);

        singlePaymentRepository.save(singlePayment);

        // Redis에서 주문 관련 데이터 삭제
        removeOrderRelatedData(memberId);
    }

    private void saveOrderDetails(OrderType orderType, Long addressId, ApproveResponseDTO approveResponseDTO) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderType(orderType);

        if (OrderType.PICKUP.getType().equals(orderType.getType())) {
            orderDetails.setPickupPlace(approveResponseDTO.getItem_name());
            orderDetails.setPickupDate(LocalDate.now());
            orderDetails.setPickupTime(LocalTime.now());
        } else if (OrderType.DELIVER.getType().equals(orderType.getType())) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new KakaoPayException(ErrorCode.ADDRESS_NOT_FOUND, null));
            orderDetails.setAddress(address);
        }

        orderDetailsRepository.save(orderDetails);
    }

    public void cancelPayment(Long memberId) {
        removeOrderRelatedData(memberId);
    }

    public void failPayment(Long memberId) {
        removeOrderRelatedData(memberId);
    }

    private void saveOrderId(Long memberId, String orderId) {
        String key = "order:" + memberId;
        redisTemplate.opsForValue().set(key, orderId, 30, TimeUnit.MINUTES);
    }

    public String getOrderId(Long memberId) {
        String key = "order:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private void removeOrderId(Long memberId) {
        String key = "order:" + memberId;
        redisTemplate.delete(key);
    }

    private void saveTransactionId(Long memberId, String tid) {
        String key = "transaction:" + memberId;
        redisTemplate.opsForValue().set(key, tid, 30, TimeUnit.MINUTES);
    }

    private String getTransactionId(Long memberId) {
        String key = "transaction:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private void removeTransactionId(Long memberId) {
        String key = "transaction:" + memberId;
        redisTemplate.delete(key);
    }

    private String getOrderType(Long memberId) {
        String key = "orderType:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private Long getAddressId(Long memberId) {
        String key = "addressId:" + memberId;
        String addressIdStr = redisTemplate.opsForValue().get(key);
        return addressIdStr != null ? Long.parseLong(addressIdStr) : null;
    }

    private void removeOrderRelatedData(Long memberId) {
        removeOrderId(memberId);
        removeTransactionId(memberId);
        removeOrderType(memberId);
        removeAddressId(memberId);
    }

    private void removeOrderType(Long memberId) {
        String key = "orderType:" + memberId;
        redisTemplate.delete(key);
    }

    private void removeAddressId(Long memberId) {
        String key = "addressId:" + memberId;
        redisTemplate.delete(key);
    }

    // 카카오가 요구한 헤더값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        return headers;
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
}