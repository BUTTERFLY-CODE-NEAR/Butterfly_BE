package com.codenear.butterfly.kakaoPay.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.kakaoPay.domain.dto.PaymentRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SinglePay", description = "**카카오페이 단건결제 API**")
public interface SinglePayControllerSwagger {

    @Operation(summary = "결제 준비", description = "결제 준비 API")
    ResponseEntity<ResponseDTO> paymentRequest(@RequestBody PaymentRequestDTO paymentRequestDto);

    @Operation(summary = "결제 성공", description = "결제 성공 API")
    void successPaymentRequest(@RequestParam("pg_token") String pgToken, HttpServletResponse response);

    @Operation(summary = "결제 취소", description = "결제 취소 API")
    void cancelPaymentRequest();

    @Operation(summary = "결제 실패", description = "결제 실패 API")
    void failPaymentRequest();
}
