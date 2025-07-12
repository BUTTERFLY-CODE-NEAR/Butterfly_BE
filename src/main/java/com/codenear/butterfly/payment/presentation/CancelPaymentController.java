package com.codenear.butterfly.payment.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.payment.application.PaymentCancelService;
import com.codenear.butterfly.payment.domain.dto.request.CancelRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/cancel")
@RequiredArgsConstructor
public class CancelPaymentController implements CancelPaymentControllerSwagger {

    private final PaymentCancelService paymentCancelService;

    @PostMapping
    public ResponseEntity<ResponseDTO> cancelPaymentRequest(@RequestBody CancelRequestDTO cancelRequestDTO,
                                                            @AuthenticationPrincipal MemberDTO memberDTO) {
        paymentCancelService.cancelPayment(cancelRequestDTO);
        return ResponseUtil.createSuccessResponse(HttpStatus.OK, "결제 취소가 성공적으로 완료되었습니다.", null);
    }
}
