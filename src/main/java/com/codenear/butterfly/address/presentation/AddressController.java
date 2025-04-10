package com.codenear.butterfly.address.presentation;

import com.codenear.butterfly.address.application.AddressService;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController implements AddressControllerSwagger {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAddresses(@AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(addressService.getAddresses(memberDTO));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ResponseDTO> getAddress(@PathVariable Long addressId) {
        return ResponseUtil.createSuccessResponse(addressService.getAddress(addressId));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO, @AuthenticationPrincipal MemberDTO memberDTO) {
        return ResponseUtil.createSuccessResponse(addressService.createAddress(addressCreateDTO, memberDTO));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO> updateAddress(@Valid @RequestBody AddressUpdateDTO addressUpdateDTO, @AuthenticationPrincipal MemberDTO memberDTO) {
        addressService.updateAddress(addressUpdateDTO, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<ResponseDTO> updateMainAddress(@PathVariable Long addressId, @AuthenticationPrincipal MemberDTO memberDTO) {
        addressService.updateMainAddress(addressId, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ResponseDTO> deleteAddress(@PathVariable Long addressId, @AuthenticationPrincipal MemberDTO memberDTO) {
        addressService.deleteAddress(addressId, memberDTO);
        return ResponseUtil.createSuccessResponse(null);
    }
}
