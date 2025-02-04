package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.auth.annotation.ValidPassword;
import com.codenear.butterfly.member.domain.enums.VerificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidPassword
public class ResetPasswordRequestDTO {
    @NotBlank
    private String identifier;
    @NotBlank
    private VerificationType type;
    @NotBlank
    private String newPassword;

}
