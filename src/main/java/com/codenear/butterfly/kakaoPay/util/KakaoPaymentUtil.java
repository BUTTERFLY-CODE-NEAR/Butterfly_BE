package com.codenear.butterfly.kakaoPay.util;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.CancelRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoPaymentUtil<T> {
    @Value("${kakao.payment.cid}")
    private String CID;

    @Value("${kakao.payment.secret-key-dev}")
    private String secretKey;

    @Value("${kakao.payment.host}")
    private String host;

    @Value("${kakao.payment.request-url}")
    private String requestUrl;

    public <T> T sendRequest(String requestType, Map<String, Object> parameters, Class<T> responseType) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        return new RestTemplate().postForObject(
                host + requestType,
                requestEntity,
                responseType);
    }

    public Map<String, Object> getKakaoPayReadyParameters(BasePaymentRequestDTO paymentRequestDTO, Long memberId, String partnerOrderId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("partner_order_id", partnerOrderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("item_name", paymentRequestDTO.getProductName());
        parameters.put("quantity", paymentRequestDTO.getQuantity());
        parameters.put("total_amount", paymentRequestDTO.getTotal());
        parameters.put("vat_amount", 0);
        parameters.put("tax_free_amount", 0);
        parameters.put("approval_url", requestUrl + "/success");
        parameters.put("cancel_url", requestUrl + "/cancel");
        parameters.put("fail_url", requestUrl + "/fail");
        parameters.put("custom_json", "{\"return_custom_url\":\"butterfly://\"}");
        return parameters;
    }

    public Map<String, Object> getKakaoPayApproveParameters(Long memberId, String orderId, String transactionId, String pgToken) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", transactionId);
        parameters.put("partner_order_id", orderId);
        parameters.put("partner_user_id", memberId.toString());
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    public Map<String, Object> getKakaoPayCancelParameters(OrderDetails orderDetails, CancelRequestDTO cancelRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", orderDetails.getTid());
        parameters.put("cancel_amount", cancelRequestDTO.getCancelAmount());
        parameters.put("cancel_tax_free_amount", 0);

        return parameters;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        return headers;
    }
}
