package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.CanceledAmount;
import com.codenear.butterfly.kakaoPay.domain.dto.CancelRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.CancelPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelPaymentService {

    @Value("${kakao.payment.cid}")
    private String CID;

    @Value("${kakao.payment.secret-key-dev}")
    private String secretKey;

    @Value("${kakao.payment.host}")
    private String host;

    private final CancelPaymentRepository cancelPaymentRepository;

    public void cancelKakaoPay(CancelRequestDTO cancelRequestDTO) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", cancelRequestDTO.tid());
        parameters.put("cancel_amount", cancelRequestDTO.cancelAmount());
        parameters.put("cancel_tax_free_amount", 0);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        RestTemplate restTemplate = new RestTemplate();

        CancelResponseDTO cancelResponseDTO = restTemplate.postForObject(
                host+"/cancel",
                requestEntity,
                CancelResponseDTO.class);

        CancelPayment cancelPayment = getCancelPayment(cancelResponseDTO);

        CanceledAmount canceledAmount = getApprovedCancelAmount(cancelResponseDTO);
        cancelPayment.setCanceledAmount(canceledAmount);

        cancelPaymentRepository.save(cancelPayment);
    }

    private CancelPayment getCancelPayment(CancelResponseDTO cancelResponseDTO) {
        CancelPayment cancelPayment = new CancelPayment();
        cancelPayment.setAid(Objects.requireNonNull(cancelResponseDTO).getAid());
        cancelPayment.setTid(cancelResponseDTO.getTid());
        cancelPayment.setCid(cancelResponseDTO.getCid());
        cancelPayment.setStatus(cancelResponseDTO.getStatus());
        cancelPayment.setPartnerOrderId(cancelResponseDTO.getPartner_order_id());
        cancelPayment.setPartnerUserId(cancelResponseDTO.getPartner_user_id());
        cancelPayment.setPaymentMethodType(cancelResponseDTO.getPayment_method_type());
        cancelPayment.setItemName(cancelResponseDTO.getItem_name());
        cancelPayment.setItemCode(cancelResponseDTO.getItem_code());
        cancelPayment.setQuantity(cancelResponseDTO.getQuantity());
        cancelPayment.setCreatedAt(cancelResponseDTO.getCreated_at());
        cancelPayment.setApprovedAt(cancelResponseDTO.getApproved_at());
        cancelPayment.setPayload(cancelResponseDTO.getPayload());
        return cancelPayment;
    }

    private CanceledAmount getApprovedCancelAmount(CancelResponseDTO cancelResponseDTO) {
        CanceledAmount canceledAmount = new CanceledAmount();
        canceledAmount.setTotal(Objects.requireNonNull(cancelResponseDTO).getAmount().getTotal());
        canceledAmount.setTaxFree(cancelResponseDTO.getAmount().getTax_free());
        canceledAmount.setVat(cancelResponseDTO.getAmount().getVat());
        canceledAmount.setPoint(cancelResponseDTO.getAmount().getPoint());
        canceledAmount.setDiscount(cancelResponseDTO.getAmount().getDiscount());
        return canceledAmount;
    }

    // 카카오가 요구한 헤더값
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "SECRET_KEY " + secretKey);
        return headers;
    }
}
