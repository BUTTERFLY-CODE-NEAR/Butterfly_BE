package com.codenear.butterfly.payment.tossPay.util;

import com.codenear.butterfly.payment.domain.PaymentRedisField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class TossPaymentUtil<T> {
    @Value("${toss.payment.secret-key}")
    private String secretKey;

    @Value("${toss.payment.host}")
    private String host;

    public <T> T sendRequest(String requestType, Map<String, Object> parameters, Class<T> responseType) {
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, getHeaders());
        return new RestTemplate().postForObject(
                host + requestType,
                requestEntity,
                responseType);
    }

    public Map<String, Object> confirmParameter(String paymentKey, String orderId, int amount) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentKey", paymentKey);
        parameters.put("orderId", orderId);
        parameters.put("amount", amount);

        return parameters;
    }

    public Map<String, String> preConfirmParameter(int quantity, int totalAmount) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(PaymentRedisField.QUANTITY.getFieldName(), String.valueOf(quantity));
        parameters.put(PaymentRedisField.TOTAL_AMOUNT.getFieldName(), String.valueOf(totalAmount));
        return parameters;
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", getAuthorization());
        return headers;
    }

    public String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
