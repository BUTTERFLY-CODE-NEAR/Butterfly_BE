package com.codenear.butterfly.auth.presentation;

import com.codenear.butterfly.auth.presentation.swagger.LogoutControllerSwagger;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController implements LogoutControllerSwagger {

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        return ResponseUtil.createSuccessResponse(null);
    }
}
