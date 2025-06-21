package com.codenear.butterfly.payment.tossPay.presentation.swagger;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.payment.domain.dto.request.DeliveryPaymentRequestDTO;
import com.codenear.butterfly.payment.domain.dto.request.PickupPaymentRequestDTO;
import com.codenear.butterfly.payment.exception.PaymentException;
import com.codenear.butterfly.payment.tossPay.domain.dto.TossPaymentCancelRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "TossPay", description = "**토스페이 결제 API**")
public interface TossPaymentControllerSwagger {

    @Operation(summary = "직거래 결제 준비", description = "직거래 결제 준비 API")
    ResponseEntity<ResponseDTO> pickupPaymentRequest(@RequestBody PickupPaymentRequestDTO paymentRequestDTO,
                                                     @AuthenticationPrincipal MemberDTO memberDTO
    );

    @Operation(summary = "배달 결제 준비", description = "배달 결제 준비 API")
    ResponseEntity<ResponseDTO> deliveryPaymentRequest(@RequestBody DeliveryPaymentRequestDTO paymentRequestDTO,
                                                       @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "결제 성공", description = "결제 성공 API")
    @ApiResponse(responseCode = "200", description = "결제 성공")
    @ApiResponse(responseCode = "40007", description = "준비단계에서 저장한 값과 승인단계에서의 값이 다를 때 결제 실패", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PaymentException.class),
            examples = @ExampleObject(value = """
                        {
                          "code": 40200,
                          "message": "결제가 실패하였습니다.",
                          "body": "유효한 주문이 아닙니다. 다시 확인해주세요."
                        }
                    """)
    ))
    ResponseEntity<ResponseDTO> tossPaymentConfirm(@AuthenticationPrincipal MemberDTO memberDTO,
                                                   @RequestParam("paymentKey") String paymentKey,
                                                   @RequestParam("orderId") String orderId,
                                                   @RequestParam("amount") int amount,
                                                   HttpServletResponse response);

    @Operation(summary = "결제 실패", description = "결제 실패 API")
    @ApiResponse(responseCode = "402", description = "결제 실패")
    ResponseEntity<ResponseDTO> tossPaymentFail(@RequestParam("memberId") Long memberId,
                                                @RequestParam("productName") String productName,
                                                @RequestParam("quantity") int quantity,
                                                HttpServletResponse response);

    @Operation(summary = "결제 취소", description = "결제 취소 API")
    void tossPaymentCancel(@RequestBody TossPaymentCancelRequestDTO cancelRequestDTO,
                           @AuthenticationPrincipal MemberDTO memberDTO);
}
