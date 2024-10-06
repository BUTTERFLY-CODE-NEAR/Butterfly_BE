package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.dto.PaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ReadyResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.SinglePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

    public ReadyResponseDTO kakaoPayReady(PaymentRequestDTO paymentRequestDTO) {
        // 카카오페이 요청 양식
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("partner_order_id", paymentRequestDTO.getOrderId());
        parameters.put("partner_user_id", paymentRequestDTO.getMemberId());
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

        kakaoPayReady = restTemplate.postForObject(
                host+"/ready", //post 요청 url
                requestEntity,
                ReadyResponseDTO.class);

        log.info("kakaoPayReady={}", kakaoPayReady);

        return kakaoPayReady;
    }

    // 결제 승인
    public ApproveResponseDTO approveResponse(String pgToken) {
        // 카카오 요청
        // todo: 사용자 인증 정보에서 user id 가져오게 수정
        // todo: order id도 동일
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", kakaoPayReady.getTid());
        parameters.put("partner_user_id", 1);
        parameters.put("partner_order_id", 1);
        parameters.put("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        ApproveResponseDTO approveResponse = restTemplate.postForObject(
                host+"/approve",
                requestEntity,
                ApproveResponseDTO.class);

        //결제 데이터 저장
//        singlePaymentRepository.save(mapper.approveResponseDtoToSinglePayment(approveResponseDTO));
        return approveResponse;

    }

    // 카카오가 요구한 헤더값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secret_key);

        return headers;
    }
}
