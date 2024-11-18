package com.codenear.butterfly.support.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.support.domain.dto.FAQResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "FAQ", description = "**FAQ API**")
public interface FAQControllerSwagger {

    @Operation(summary = "FAQ 리스트", description = "FAQ 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = FAQResponse.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getFAQs();
}
