package com.codenear.butterfly.address.application;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.address.domain.Spot;
import com.codenear.butterfly.address.domain.dto.AddressAddResponseDTO;
import com.codenear.butterfly.address.domain.dto.AddressCreateDTO;
import com.codenear.butterfly.address.domain.dto.AddressResponse;
import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.address.domain.dto.SpotResponseDTO;
import com.codenear.butterfly.address.domain.repository.AddressRepository;
import com.codenear.butterfly.address.domain.repository.SpotRepository;
import com.codenear.butterfly.address.exception.AddressException;
import com.codenear.butterfly.member.application.MemberService;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.codenear.butterfly.global.exception.ErrorCode.SERVER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private static final double EARTH_RADIUS_METERS = 6371000;
    private final MemberService memberService;
    private final AddressRepository addressRepository;
    private final SpotRepository spotRepository;

    public List<AddressResponse> getAddresses(MemberDTO memberDTO) {
        LinkedList<Address> addresses = addressRepository.findAllByMemberId(memberDTO.getId());

        moveMainAddress(addresses); // 메인 주소 가장 상단 배치

        return addresses.stream()
                .map(address -> AddressResponse.fromEntity(address, calculateDeliveryFee(address)))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public AddressResponse getAddress(Long addressId) {
        Address address = loadAddress(addressId);

        return AddressResponse.fromEntity(address, calculateDeliveryFee(address));
    }

    public AddressAddResponseDTO createAddress(AddressCreateDTO addressCreateDTO, MemberDTO memberDTO) {
        Member member = memberService.loadMemberByMemberId(memberDTO.getId());

        Address address = Address.builder()
                .addressName(addressCreateDTO.getAddressName())
                .address(addressCreateDTO.getAddress())
                .detailedAddress(addressCreateDTO.getDetailedAddress())
                .entrancePassword(addressCreateDTO.getEntrancePassword())
                .isMainAddress(member.getAddresses().isEmpty()) // 첫 주소 등록 시, 메인 주소로 설정
                .latitude(addressCreateDTO.getLatitude())
                .longitude(addressCreateDTO.getLongitude())
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

        address.updateAddress(addressUpdateDTO);
    }

    /**
     * 사용자의 메인 주소를 기준으로 가장 가까운 스팟 계산
     *
     * @param addressId 주소 아이디
     * @return 가장 가까운 스팟
     */
    public SpotResponseDTO getNearSpot(Long addressId) {
        Address address = loadAddress(addressId);
        List<Spot> spots = spotRepository.findAll();

        return spots.stream()
                .min(Comparator.comparingInt(spot ->
                        calculateDistance(address.getLatitude(), address.getLongitude(), spot)))
                .map(SpotResponseDTO::fromSpot)
                .orElse(null);
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

    private Address loadAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressException(SERVER_ERROR, null));
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

    private Integer calculateDeliveryFee(Address address) {
        List<Spot> spots = spotRepository.findAll();

        Spot nearSpot = spots.stream()
                .min(Comparator.comparingInt(spot ->
                        calculateDistance(address.getLatitude(), address.getLongitude(), spot)))
                .orElse(null);

        int distance = calculateDistance(address.getLatitude(), address.getLongitude(), nearSpot);

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

    /**
     * 사용자의 메인 주소와 스팟의 거리를 계산한다.
     *
     * @param mainAddressLat 메인 주소지에 대한 위도
     * @param mainAddressLon 메인 주소지에 대한 경도
     * @param spot           스팟
     * @return 두 지점 사이의 거리
     */
    private int calculateDistance(double mainAddressLat, double mainAddressLon, Spot spot) {
        // 위도와 경도를 라디안으로 변환
        double mainLatRad = Math.toRadians(mainAddressLat);
        double mainLonRad = Math.toRadians(mainAddressLon);
        double spotLatRad = Math.toRadians(spot.getLatitude());
        double spotLonRad = Math.toRadians(spot.getLongitude());

        // 위도 및 경도 차이 계산
        double latDiff = spotLatRad - mainLatRad;
        double lonDiff = spotLonRad - mainLonRad;

        // 하버사인 공식
        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.cos(mainLatRad) * Math.cos(spotLatRad) *
                        Math.pow(Math.sin(lonDiff / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return roundDownDistance(EARTH_RADIUS_METERS * c);
    }

    /**
     * double인 거리값을 int로 변환
     *
     * @param distance 거리값
     * @return int
     */
    private int roundDownDistance(Double distance) {
        return (int) Math.floor(distance);
    }
}
