package com.codenear.butterfly.kakaoPay.application;

import com.codenear.butterfly.kakaoPay.domain.CancelPayment;
import com.codenear.butterfly.kakaoPay.domain.CanceledAmount;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.CancelResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.request.CancelRequestDTO;
import com.codenear.butterfly.kakaoPay.domain.repository.CancelPaymentRepository;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.kakaoPay.util.KakaoPaymentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelPaymentService {
    private final CancelPaymentRepository cancelPaymentRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final KakaoPaymentUtil<Object> kakaoPaymentUtil;

    public void cancelKakaoPay(CancelRequestDTO cancelRequestDTO) {

        OrderDetails orderDetails = orderDetailsRepository.findByOrderCode(cancelRequestDTO.getOrderCode());

        Map<String, Object> parameters = kakaoPaymentUtil.getKakaoPayCancelParameters(orderDetails,cancelRequestDTO);

        CancelResponseDTO cancelResponseDTO = kakaoPaymentUtil.sendRequest("/cancel",parameters,CancelResponseDTO.class);

        CancelPayment cancelPayment = CancelPayment.builder().cancelResponseDTO(cancelResponseDTO).build();

        CanceledAmount canceledAmount = CanceledAmount.builder().cancelResponseDTO(cancelResponseDTO).build();
        cancelPayment.addCanceledAmount(canceledAmount);

        orderDetails.updateOrderStatus(OrderStatus.CANCELED);
        cancelPaymentRepository.save(cancelPayment);
    }
}
