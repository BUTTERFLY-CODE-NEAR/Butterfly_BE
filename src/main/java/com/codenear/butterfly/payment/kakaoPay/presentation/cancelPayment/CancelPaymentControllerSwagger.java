package com.codenear.butterfly.payment.kakaoPay.presentation.cancelPayment;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "CancelPayment", description = "**카카오페이 결제취소 API**")
public interface CancelPaymentControllerSwagger {

    @Operation(summary = "결제 취소(환불)", description = "결제 취소 API")
    ResponseEntity<ResponseDTO> cancelPaymentRequest(@RequestBody CancelRequestDTO cancelRequestDTO,
                                                     @AuthenticationPrincipal MemberDTO memberDTO);
}
