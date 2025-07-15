package com.codenear.butterfly.sms.config;

import com.codenear.butterfly.sms.domain.dto.CloudSmsResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SmsConfig {

    @Value("${cloud.access-key}")
    private String accessKey;

    @Value("${cloud.secret-key}")
    private String secretKey;

    @Value("${cloud-sms.service-id}")
    private String serviceId;

    @Value("${cloud-sms.sender-phone}")
    private String phone;

    @Value("${cloud-sms.host}")
    private String host;

    public CloudSmsResponseDTO sendRequest(Map<String, Object> parameters) {
        String urlPath = "/sms/v2/services/" + serviceId + "/messages";
        String url = host + urlPath;
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, getHeaders(urlPath));
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, requestEntity, CloudSmsResponseDTO.class);
    }

    public CloudSmsResponseDTO sendSms(String to, String content) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "SMS");
        parameters.put("from", phone);
        parameters.put("content", content);
        parameters.put("messages", Collections.singletonList(Map.of("to", to)));

        return sendRequest(parameters);
    }

    private HttpHeaders getHeaders(String requestType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-ncp-apigw-timestamp", timestamp);
        headers.add("x-ncp-iam-access-key", accessKey);
        headers.add("x-ncp-apigw-signature-v2", generateSignature(requestType, timestamp));
        headers.add("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    private String generateSignature(String requestType, String timestamp) {
        try {
            String method = "POST";
            String message = method + " " + requestType + "\n" + timestamp + "\n" + accessKey;
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}