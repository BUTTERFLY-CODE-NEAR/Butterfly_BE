package com.codenear.butterfly.address.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressAddResponseDTO(
        @Schema(description = "주소 ID") Long id) {
}
