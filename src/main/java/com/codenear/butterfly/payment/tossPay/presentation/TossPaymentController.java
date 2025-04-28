package com.codenear.butterfly.payment.tossPay.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.payment.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.payment.exception.PaymentException;
import com.codenear.butterfly.payment.tossPay.application.TossPaymentService;
import com.codenear.butterfly.payment.tossPay.presentation.swagger.TossPaymentControllerSwagger;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payment/toss")
public class TossPaymentController implements TossPaymentControllerSwagger {
    private final TossPaymentService tossPaymentService;

    @PostMapping("/ready/pickup")
    public ResponseEntity<ResponseDTO> pickupPaymentRequest(@RequestBody PickupPaymentRequestDTO paymentRequestDTO,
                                                            @AuthenticationPrincipal MemberDTO memberDTO) {
        tossPaymentService.paymentReady(paymentRequestDTO, memberDTO.getId(), "pickup");
        return ResponseUtil.createSuccessResponse(null);
    }

    @PostMapping("/ready/delivery")
    public ResponseEntity<ResponseDTO> deliveryPaymentRequest(@RequestBody DeliveryPaymentRequestDTO paymentRequestDTO,
                                                              @AuthenticationPrincipal MemberDTO memberDTO) {
        tossPaymentService.paymentReady(paymentRequestDTO, memberDTO.getId(), "deliver");
        return ResponseUtil.createSuccessResponse(null);
    }

    @GetMapping("/approve")
    public void tossPaymentConfirm(@AuthenticationPrincipal MemberDTO memberDTO,
                                   @RequestParam("paymentKey") String paymentKey,
                                   @RequestParam("orderId") String orderId,
                                   @RequestParam("amount") int amount,
                                   HttpServletResponse response) {
        tossPaymentService.confirm(memberDTO.getId(), paymentKey, orderId, amount);
        try {
            response.sendRedirect("butterfly://kakaopay/success");
        } catch (IOException e) {
            throw new PaymentException(ErrorCode.PAYMENT_REDIRECT_FAILED, null);
        }
    }
}
