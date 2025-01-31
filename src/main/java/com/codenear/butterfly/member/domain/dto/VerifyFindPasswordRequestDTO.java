package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.member.domain.enums.VerificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyFindPasswordRequestDTO {
    @NotBlank
    private String identifier;
    @NotBlank
    private VerificationType type;
    @NotBlank
    private String certifyCode;
}
