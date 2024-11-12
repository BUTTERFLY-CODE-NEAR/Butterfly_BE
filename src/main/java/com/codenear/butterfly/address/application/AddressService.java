package com.codenear.butterfly.address.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.address.domain.dto.AddressAddResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.address.exception.AddressException;
import com.codenear.butterfly.geocoding.application.GeocodingService;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final MemberService memberService;
    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;

    public List<AddressResponseDTO> getAddresses(MemberDTO memberDTO) {
        LinkedList<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());

        moveMainAddress(addresses); // 메인 주소 가장 상단 배치

        return addresses.stream()
                .map(this::convertToAddressResponseDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public AddressResponseDTO getAddress(Long addressId) {
        Optional<Address> optAddress = addressRepository.findById(addressId);

        return optAddress
                .map(this::convertToAddressResponseDTO)
                .orElse(null);
    }

    public AddressAddResponseDTO createAddress(AddressCreateDTO addressCreateDTO, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());

        Address address = Address.builder()
                .addressName(addressCreateDTO.getAddressName())
                .address(addressCreateDTO.getAddress())
                .detailedAddress(addressCreateDTO.getDetailedAddress())
                .entrancePassword(addressCreateDTO.getEntrancePassword())
                .distance(geocodingService.fetchDistance(addressCreateDTO.getAddress()))
                .isMainAddress(member.getAddresses().isEmpty()) // 첫 주소 등록 시, 메인 주소로 설정
                .member(member)
                .build();

        Address saveAddress = addressRepository.save(address);

        return new AddressAddResponseDTO(saveAddress.getId());
    }

    public void updateAddress(AddressUpdateDTO addressUpdateDTO, MemberDTO memberDTO) {
        Address address = addressRepository.findById(addressUpdateDTO.getId())
                .orElseThrow(() -> new AddressException(ErrorCode.SERVER_ERROR, null));

        if (!address.getMember().getId().equals(memberDTO.getId()))
            throw new AddressException(ErrorCode.SERVER_ERROR, null);

        address.updateAddress(addressUpdateDTO);
    }

    public void updateMainAddress(Long addressId, MemberDTO memberDTO) {
        LinkedList<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());

        addresses.stream()
                .filter(Address::isMainAddress)
                .findFirst()
                .ifPresent(mainAddress -> mainAddress.setMainAddress(false));

        Address newAddress = addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressException(ErrorCode.SERVER_ERROR, null));

        newAddress.setMainAddress(true);
    }

    public void deleteAddress(Long addressId, MemberDTO memberDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(ErrorCode.SERVER_ERROR, null));

        if (address.isMainAddress()) {
            List<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());
            Collections.reverse(addresses);

            Optional<Address> first = addresses.stream()
                    .filter(findAddress -> !address.getId().equals(findAddress.getId()))
                    .findFirst();

            first.ifPresent(value -> value.setMainAddress(true));
        }

        addressRepository.delete(address);
    }

    private AddressResponseDTO convertToAddressResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getAddressName(),
                address.getAddress(),
                address.getDetailedAddress(),
                address.getEntrancePassword(),
                address.isMainAddress()
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
