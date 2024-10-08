package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.dto.PaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ReadyResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.SinglePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SinglePaymentService {

    @Value("${kakao.payment.cid}")
    private String CID;

    @Value("${kakao.payment.secret-key-dev}")
    private String secret_key;

    @Value("${kakao.payment.host}")
    private String host;

    private ReadyResponseDTO kakaoPayReady;
    private final SinglePaymentRepository singlePaymentRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final SinglePaymentMapper singlePaymentMapper;

    public ReadyResponseDTO kakaoPayReady(PaymentRequestDTO paymentRequestDTO, Long memberId) {
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
        parameters.put("approval_url", "http://localhost:8080/payment/success"); // 성공 시 redirect url http://localhost:8080/success?pg_token=2c44d553eb444534f36d
        parameters.put("cancel_url", "http://localhost:8080/payment/cancel"); // 취소 시 redirect url
        parameters.put("fail_url", "http://localhost:8080/payment/fail"); // 실패 시 redirect url

        // 파라미터, 헤더
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        log.info("kakaoPayReadyURL={}", host+"/ready");

        ReadyResponseDTO kakaoPayReady = restTemplate.postForObject(
                host+"/ready",
                requestEntity,
                ReadyResponseDTO.class);

        log.info("kakaoPayReady={}", kakaoPayReady);

        // Redis에 주문 ID와 거래 ID 저장
        saveOrderId(memberId, partnerOrderId);
        saveTransactionId(memberId, Objects.requireNonNull(kakaoPayReady).getTid());

        return kakaoPayReady;
    }

    @Transactional
    public void approveResponse(String pgToken, Long memberId) {
        String orderId = getOrderId(memberId);
        String transactionId = getTransactionId(memberId);

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

        ApproveResponseDTO approveResponse = restTemplate.postForObject(
        restTemplate.postForObject(
                host+"/approve",
                requestEntity,
                ApproveResponseDTO.class);

        //결제 데이터 저장
//        singlePaymentRepository.save(mapper.approveResponseDtoToSinglePayment(approveResponseDTO));
        return approveResponse;
        // Redis에서 주문 ID와 거래 ID 삭제
        removeOrderId(memberId);
        removeTransactionId(memberId);
    }

    public void cancelPayment(Long memberId) {
        removeOrderId(memberId);
        removeTransactionId(memberId);
    }

    public void failPayment(Long memberId) {
        removeOrderId(memberId);
        removeTransactionId(memberId);
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

    // 카카오가 요구한 헤더값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secret_key);
        return headers;
    }
}