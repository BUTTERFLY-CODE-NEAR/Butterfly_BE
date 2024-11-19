package com.codenear.butterfly.support.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.support.application.InquiryService;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterDTO;
import com.codenear.butterfly.support.presentation.swagger.InquiryControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.codenear.butterfly.global.util.ResponseUtil.createSuccessResponse;

@RestController
@RequestMapping("/support/inquiry")
@RequiredArgsConstructor
public class InquiryController implements InquiryControllerSwagger {
    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getInquiryList(@AuthenticationPrincipal MemberDTO memberDTO) {
        List<InquiryListDTO> inquiryList = inquiryService.getInquiryList(memberDTO);
        return createSuccessResponse(inquiryList);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registerInquiry(@Valid @RequestBody InquiryRegisterDTO inquiryRegisterDTO, @AuthenticationPrincipal MemberDTO memberDTO) {
        inquiryService.registerInquiry(inquiryRegisterDTO, memberDTO);
        return createSuccessResponse(null);
    }
}
