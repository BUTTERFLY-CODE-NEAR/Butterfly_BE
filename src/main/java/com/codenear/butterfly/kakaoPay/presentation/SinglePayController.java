package com.codenear.butterfly.kakaoPay.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.kakaoPay.application.SinglePaymentService;
import com.codenear.butterfly.kakaoPay.domain.dto.PaymentRequestDTO;
import com.codenear.butterfly.kakaoPay.exception.KakaoPayException;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class SinglePayController implements SinglePayControllerSwagger {

    private final SinglePaymentService singlePaymentService;

    //결제 요청
    @PostMapping("/ready")
    public ResponseEntity<ResponseDTO> paymentRequest(@RequestBody PaymentRequestDTO paymentRequestDTO,
                                                      @AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(singlePaymentService.kakaoPayReady(paymentRequestDTO, memberDTO.getId())); // "redirect: https://online-pay.kakao.com/mockup/v1/80db866d0c2bbeb45c69d1f5603feb3be67e492edfefc0018bd2991c9237d68a/info";
    }

    // 예시: http://localahost:8080/success?pg_token=2c44d553eb444534f36d
    // 결제 성공
    @GetMapping("/success")
    public void successPaymentRequest(@RequestParam("pg_token") String pgToken,
                                      @AuthenticationPrincipal MemberDTO memberDTO) {
        singlePaymentService.approveResponse(pgToken, memberDTO.getId());
    }

    // 결제준비 취소
    @GetMapping("/cancel")
    public void cancelPaymentRequest(@AuthenticationPrincipal MemberDTO memberDTO) {
        singlePaymentService.cancelPayment(memberDTO.getId());
        throw new KakaoPayException(ErrorCode.PAY_CANCEL, null);
    }

    // 결제 실패
    @GetMapping("/fail")
    public void failPaymentRequest(@AuthenticationPrincipal MemberDTO memberDTO) {
        singlePaymentService.failPayment(memberDTO.getId());
        throw new KakaoPayException(ErrorCode.PAY_FAILED, null);
    }
}
