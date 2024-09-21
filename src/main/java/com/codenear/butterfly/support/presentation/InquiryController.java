package com.codenear.butterfly.support.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.support.application.InquiryService;
import com.codenear.butterfly.support.domain.dto.InquiryListDTO;
import com.codenear.butterfly.support.domain.dto.InquiryRegisterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.codenear.butterfly.global.util.ResponseUtil.*;

@RestController
@RequestMapping("/support/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getInquiryList(@AuthenticationPrincipal Member loginMember) {
        List<InquiryListDTO> inquiryList = inquiryService.getInquiryList(loginMember);
        return createSuccessResponse(inquiryList);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> registerInquiry(@Valid @RequestBody InquiryRegisterDTO dto, @AuthenticationPrincipal Member loginMember) {
        inquiryService.registerInquiry(dto, loginMember);
        return createSuccessResponse(null);
    }
}
