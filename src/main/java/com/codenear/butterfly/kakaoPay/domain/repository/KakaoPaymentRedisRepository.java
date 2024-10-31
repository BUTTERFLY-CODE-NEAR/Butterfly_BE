package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.DeliveryPaymentRequestDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class KakaoPaymentRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public KakaoPaymentRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOrderId(Long memberId, String orderId) {
        String key = "order:" + memberId;
        redisTemplate.opsForValue().set(key, orderId, 30, TimeUnit.MINUTES);
    }

    public String getOrderId(Long memberId) {
        String key = "order:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeOrderId(Long memberId) {
        String key = "order:" + memberId;
        redisTemplate.delete(key);
    }

    public void saveTransactionId(Long memberId, String tid) {
        String key = "transaction:" + memberId;
        redisTemplate.opsForValue().set(key, tid, 30, TimeUnit.MINUTES);
    }

    public String getTransactionId(Long memberId) {
        String key = "transaction:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeTransactionId(Long memberId) {
        String key = "transaction:" + memberId;
        redisTemplate.delete(key);
    }

    public void saveOrderRelatedData(Long memberId, String orderType, BasePaymentRequestDTO paymentRequestDTO) {
        saveOrderType(memberId, orderType);
        saveAddressId(memberId, paymentRequestDTO);
        saveOptionName(memberId, paymentRequestDTO);
    }

    private void saveOrderType(Long memberId, String orderType) {
        String key = "orderType:" + memberId;
        redisTemplate.opsForValue().set(key, orderType, 30, TimeUnit.MINUTES);
    }

    public String getOrderType(Long memberId) {
        String key = "orderType:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private void removeOrderType(Long memberId) {
        String key = "orderType:" + memberId;
        redisTemplate.delete(key);
    }

    private void saveAddressId(Long memberId, BasePaymentRequestDTO paymentRequestDTO) {
        if (paymentRequestDTO instanceof DeliveryPaymentRequestDTO) {
            String key = "addressId:" + memberId;
            redisTemplate.opsForValue().set(key, ((DeliveryPaymentRequestDTO) paymentRequestDTO).getAddressId().toString(), 30, TimeUnit.MINUTES);
        }
    }

    public Long getAddressId(Long memberId) {
        String key = "addressId:" + memberId;
        String addressIdStr = redisTemplate.opsForValue().get(key);
        return addressIdStr != null ? Long.parseLong(addressIdStr) : null;
    }

    private void removeAddressId(Long memberId) {
        String key = "addressId:" + memberId;
        redisTemplate.delete(key);
    }

    private void saveOptionName(Long memberId, BasePaymentRequestDTO paymentRequestDTO) {
        String optionName = paymentRequestDTO.getOptionName();
        if (optionName != null) {
            String key = "optionName:" + memberId;
            redisTemplate.opsForValue().set(key, optionName, 30, TimeUnit.MINUTES);
        }
    }

    public String getOptionName(Long memberId) {
        String key = "optionName:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private void removeOptionName(Long memberId) {
        String key = "optionName:" + memberId;
        redisTemplate.delete(key);
    }

    public void removeOrderRelatedData(Long memberId) {
        removeOrderId(memberId);
        removeTransactionId(memberId);
        removeOrderType(memberId);
        removeAddressId(memberId);
        removeOptionName(memberId);
    }

    public void savePaymentStatus(Long memberId, String status) {
        String key = "paymentStatus:" + memberId;
        redisTemplate.opsForValue().set(key, status, 30, TimeUnit.MINUTES);
    }

    public String getPaymentStatus(Long memberId) {
        String key = "paymentStatus:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    public void removePaymentStatus(String key) {
        redisTemplate.delete(key);
    }
}