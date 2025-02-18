package com.codenear.butterfly.member.domain.dto;

import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.enums.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPasswordRequestDTO {
    @NotBlank
    private String identifier;
    @NotNull
    private VerificationType type;
}
