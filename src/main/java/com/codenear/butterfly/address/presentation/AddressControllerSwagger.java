package com.codenear.butterfly.address.presentation;

import com.codenear.butterfly.address.domain.dto.AddressAddResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponse;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.address.domain.dto.SpotResponseDTO;
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
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getAddresses(@AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "주소 상세", description = "주소 상세 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> getAddress(@PathVariable Long addressId);

    @Operation(summary = "주소 추가", description = "주소 추가 API")
    @ApiResponses({
            @ApiResponse(responseCode = "body", description = "응답 메시지 예시",
                    content = @Content(schema = @Schema(implementation = AddressAddResponseDTO.class))),
            @ApiResponse(responseCode = "200", description = "Success")
    })
    ResponseEntity<ResponseDTO> createAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "주소 수정", description = "주소 수정 API")
    ResponseEntity<ResponseDTO> updateAddress(@Valid @RequestBody AddressUpdateDTO addressUpdateDTO, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "메인 주소 변경", description = "메인 주소 변경 API")
    ResponseEntity<ResponseDTO> updateMainAddress(@PathVariable Long addressId, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "주소 삭제", description = "주소 삭제 API (메인 주소 삭제 : 마지막 주소로 메인 변경)")
    ResponseEntity<ResponseDTO> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal MemberDTO memberDTO);

    @Operation(summary = "가까운 스팟 반환", description = "사용자의 메인 주소를 기준으로 가장 가까운 스팟 반환 API")
    @ApiResponse(responseCode = "200", description = "SpotResponseDTO", content = @Content(schema = @Schema(implementation = SpotResponseDTO.class)))
    ResponseEntity<ResponseDTO> getNearSpot(@PathVariable Long addressId);
}
