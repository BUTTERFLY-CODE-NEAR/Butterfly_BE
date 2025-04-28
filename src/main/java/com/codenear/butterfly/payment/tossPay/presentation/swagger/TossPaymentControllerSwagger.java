package com.codenear.butterfly.payment.tossPay.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.payment.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.PickupPaymentRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "TossPay", description = "**토스페이 결제 승인 API**")
public interface TossPaymentControllerSwagger {

    @Operation(summary = "직거래 결제 준비", description = "직거래 결제 준비 API")
    ResponseEntity<ResponseDTO> pickupPaymentRequest(@RequestBody PickupPaymentRequestDTO paymentRequestDTO,
                                                     @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "배달 결제 준비", description = "배달 결제 준비 API")
    ResponseEntity<ResponseDTO> deliveryPaymentRequest(@RequestBody DeliveryPaymentRequestDTO paymentRequestDTO,
                                                       @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "결제 성공", description = "결제 성공 API")
    void tossPaymentConfirm(@AuthenticationPrincipal MemberDTO memberDTO,
                            @RequestParam("paymentKey") String paymentKey,
                            @RequestParam("orderId") String orderId,
                            @RequestParam("amount") int amount,
                            HttpServletResponse response);
}
