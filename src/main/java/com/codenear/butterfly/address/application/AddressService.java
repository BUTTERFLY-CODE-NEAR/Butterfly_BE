package com.codenear.butterfly.address.application;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.AddressRepository;
import com.codenear.butterfly.address.domain.dto.AddressAddResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponse;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.address.exception.AddressException;
import com.codenear.butterfly.geocoding.application.GeocodingService;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final MemberService memberService;
    private final AddressRepository addressRepository;
    private final GeocodingService geocodingService;

    public List<AddressResponse> getAddresses(MemberDTO memberDTO) {
        LinkedList<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());

        moveMainAddress(addresses); // 메인 주소 가장 상단 배치

        return addresses.stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public AddressResponse getAddress(Long addressId) {
        Address address = loadAddress(addressId);
        return AddressResponse.fromEntity(address);
    }

    public AddressAddResponseDTO createAddress(AddressCreateDTO addressCreateDTO, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());
        int distance = geocodingService.fetchDistance(addressCreateDTO.getAddress());

        Address address = Address.builder()
                .addressName(addressCreateDTO.getAddressName())
                .address(addressCreateDTO.getAddress())
                .detailedAddress(addressCreateDTO.getDetailedAddress())
                .entrancePassword(addressCreateDTO.getEntrancePassword())
                .distance(distance)
                .deliveryFee(calculateDeliveryFee(distance))
                .isMainAddress(member.getAddresses().isEmpty()) // 첫 주소 등록 시, 메인 주소로 설정
                .member(member)
                .build();

        Address saveAddress = addressRepository.save(address);

        return new AddressAddResponseDTO(saveAddress.getId());
    }

    public void updateAddress(AddressUpdateDTO addressUpdateDTO, MemberDTO memberDTO) {
        Address address = loadAddress(addressUpdateDTO.getId());

        if (!address.getMember().getId().equals(memberDTO.getId())) {
            throw new AddressException(SERVER_ERROR, null);
        }

        int distance = geocodingService.fetchDistance(addressUpdateDTO.getAddress());
        Integer deliveryFee = calculateDeliveryFee(distance);
        address.updateAddress(addressUpdateDTO, deliveryFee);
    }

    private Address loadAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(SERVER_ERROR, null));
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
                .orElseThrow(() -> new AddressException(SERVER_ERROR, null));

        newAddress.setMainAddress(true);
    }

    public void deleteAddress(Long addressId, MemberDTO memberDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(SERVER_ERROR, null));

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

    private void moveMainAddress(LinkedList<Address> addresses) {
        addresses.stream()
                .filter(Address::isMainAddress)
                .findFirst()
                .ifPresent(mainAddress -> {
                    addresses.remove(mainAddress);
                    addresses.addFirst(mainAddress);
                });
    }

    private Integer calculateDeliveryFee(int distance) {
        if (distance <= 300) {
            return 1000;
        }

        if (distance <= 600) {
            return 1500;
        }

        if (distance <= 1000) {
            return 2000;
        }
        return null;
    }
}
