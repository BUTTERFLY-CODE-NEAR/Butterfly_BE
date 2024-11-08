package com.codenear.butterfly.kakaoPay.domain.repository;

import com.codenear.butterfly.kakaoPay.domain.dto.request.BasePaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.PickupPaymentRequestDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Repository
public class KakaoPaymentRedisRepository {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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

        if (paymentRequestDTO instanceof PickupPaymentRequestDTO pickupDTO) {
            savePickupPlace(memberId, pickupDTO.getPickupPlace());
            savePickupDate(memberId, pickupDTO.getPickupDate());
            savePickupTime(memberId, pickupDTO.getPickupTime());
        }
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

    private void savePickupDate(Long memberId, LocalDate pickupDate) {
        if (pickupDate != null) {
            String key = "pickupDate:" + memberId;
            String dateStr = pickupDate.format(DATE_FORMATTER);
            redisTemplate.opsForValue().set(key, dateStr, 30, TimeUnit.MINUTES);
        }
    }

    public LocalDate getPickupDate(Long memberId) {
        String key = "pickupDate:" + memberId;
        String dateStr = redisTemplate.opsForValue().get(key);
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    private void removePickupDate(Long memberId) {
        String key = "pickupDate:" + memberId;
        redisTemplate.delete(key);
    }

    private void savePickupTime(Long memberId, LocalTime pickupTime) {
        if (pickupTime != null) {
            String key = "pickupTime:" + memberId;
            String timeStr = pickupTime.format(TIME_FORMATTER);
            redisTemplate.opsForValue().set(key, timeStr, 30, TimeUnit.MINUTES);
        }
    }

    public LocalTime getPickupTime(Long memberId) {
        String key = "pickupTime:" + memberId;
        String timeStr = redisTemplate.opsForValue().get(key);
        return timeStr != null ? LocalTime.parse(timeStr, TIME_FORMATTER) : null;
    }

    private void removePickupTime(Long memberId) {
        String key = "pickupTime:" + memberId;
        redisTemplate.delete(key);
    }

    private void savePickupPlace(Long memberId, String pickupPlace) {
        if (pickupPlace != null) {
            String key = "pickupPlace:" + memberId;
            redisTemplate.opsForValue().set(key, pickupPlace, 30, TimeUnit.MINUTES);
        }
    }

    public String getPickupPlace(Long memberId) {
        String key = "pickupPlace:" + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    private void removePickupPlace(Long memberId) {
        String key = "pickupPlace:" + memberId;
        redisTemplate.delete(key);
    }

    public void removeOrderRelatedData(Long memberId) {
        removeOrderId(memberId);
        removeTransactionId(memberId);
        removeOrderType(memberId);
        removeAddressId(memberId);
        removeOptionName(memberId);
        removePickupDate(memberId);
        removePickupTime(memberId);
        removePickupPlace(memberId);
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
