package com.codenear.butterfly.address.presentation;

import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Address", description = "**주소 API**")
public interface AddressControllerSwagger {

    @Operation(summary = "주소 목록", description = "주소 목록 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getAddresses(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "주소 상세", description = "주소 상세 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = AddressResponseDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getAddress(@PathVariable Long addressId);

    @Operation(summary = "주소 추가", description = "주소 추가 API")
    ResponseEntity<ResponseDTO> createAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "주소 수정", description = "주소 수정 API")
    ResponseEntity<ResponseDTO> updateAddress(@Valid @RequestBody AddressUpdateDTO addressUpdateDTO, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "메인 주소 변경", description = "메인 주소 변경 API")
    ResponseEntity<ResponseDTO> updateMainAddress(@PathVariable Long addressId, @AuthenticationPrincipal MemberDTO memberDTO);
}
