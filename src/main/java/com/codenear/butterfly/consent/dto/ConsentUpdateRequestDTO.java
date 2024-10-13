package com.codenear.butterfly.consent.dto;

import com.codenear.butterfly.consent.domain.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(title = "수신 동의 변경 JSON", description = "수신 동의 변경 시 정의되는 JSON 데이터 입니다.")
@Getter
public class ConsentUpdateRequestDTO {

    @Schema(description = "동의 종류")
    private ConsentType consentType;
}
