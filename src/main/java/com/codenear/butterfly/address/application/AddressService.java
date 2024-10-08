package com.codenear.butterfly.address.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.address.exception.AddressException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final MemberService memberService;
    private final AddressRepository addressRepository;

    public List<AddressResponseDTO> getAddresses(MemberDTO memberDTO) {
        LinkedList<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());

        moveMainAddress(addresses); // 메인 주소 가장 상단 배치

        return addresses.stream()
                .map(this::convertToAddressResponseDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public AddressResponseDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(ErrorCode.SERVER_ERROR, null));

        return convertToAddressResponseDTO(address);
    }

    public void createAddress(AddressCreateDTO addressCreateDTO, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());

        Address address = Address.builder()
                .addressName(addressCreateDTO.getAddressName())
                .address(addressCreateDTO.getAddress())
                .detailedAddress(addressCreateDTO.getDetailedAddress())
                .entrancePassword(addressCreateDTO.getEntrancePassword())
                .isMainAddress(member.getAddresses().isEmpty()) // 첫 주소 등록 시, 메인 주소로 설정
                .member(member)
                .build();

        addressRepository.save(address);
    }

    public void updateAddress(AddressUpdateDTO addressUpdateDTO) {
        Address address = addressRepository.findById(addressUpdateDTO.getId())
                .orElseThrow(() -> new AddressException(ErrorCode.SERVER_ERROR, null));

        address.updateAddress(addressUpdateDTO);
    }

    private AddressResponseDTO convertToAddressResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getAddressName(),
                address.getAddress(),
                address.getDetailedAddress(),
                address.getEntrancePassword()
        );
    }

    private void moveMainAddress(LinkedList<Address> addresses) {
        addresses.stream()
                .filter(Address::isMainAddress)
                .findFirst()
                .ifPresent(mainAddress -> {
                    addresses.remove(mainAddress);
                    addresses.addFirst(mainAddress);
                });
    }
}
