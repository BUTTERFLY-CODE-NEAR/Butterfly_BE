package com.codenear.butterfly.notify.alarm.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.notify.alarm.application.RestockService;
import com.codenear.butterfly.notify.alarm.domain.dto.RestockResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify/alarm")
public class RestockController implements RestockControllerSwagger {
    private final RestockService restockService;

    @PostMapping("/{product_id}/restock")
    public ResponseEntity<ResponseDTO> createRestock(@AuthenticationPrincipal MemberDTO member,
                                                     @PathVariable(name = "product_id") Long productId) {
        RestockResponseDTO applyRestock = restockService.createRestock(member.getId(), productId);
        return ResponseUtil.createSuccessResponse(applyRestock);
    }
}
