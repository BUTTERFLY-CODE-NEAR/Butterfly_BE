package com.codenear.butterfly.address.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(title = "주소 추가 JSON", description = "주소 추가 시 정의되는 JSON 데이터 입니다.")
@Getter
public class AddressCreateDTO {

    @Schema(description = "주소 이름", example = "집")
    @NotNull
    private String addressName;

    @Schema(description = "주소", example = "강릉시 구원로 54")
    @NotNull
    private String address;

    @Schema(description = "상세 주소", example = "3층 302호")
    @NotNull
    private String detailedAddress;

    @Schema(description = "현관 비밀번호", example = "#1234")
    private String entrancePassword;

    @Schema(description = "위도", example = "37.30574")
    private double latitude;

    @Schema(description = "경도", example = "127.92251")
    private double longitude;
}
