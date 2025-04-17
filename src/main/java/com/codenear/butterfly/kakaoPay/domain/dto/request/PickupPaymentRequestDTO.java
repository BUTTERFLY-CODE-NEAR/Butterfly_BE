package com.codenear.butterfly.kakaoPay.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(title = "직거래 결제 요청 JSON", description = "직거래 결제 요청 시 사용되는 JSON 데이터입니다.")
@Getter
public class PickupPaymentRequestDTO extends BasePaymentRequestDTO {

    @Schema(description = "픽업 장소", example = "W4")
    private String pickupPlace;

    @Schema(description = "픽업 날짜", example = "2024-10-21")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate pickupDate;

    @Schema(description = "픽업 시간", example = "14:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime pickupTime;
}