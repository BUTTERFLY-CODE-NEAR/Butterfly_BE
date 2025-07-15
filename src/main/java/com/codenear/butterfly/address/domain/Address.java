package com.codenear.butterfly.address.domain;

import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressName;

    private String address;

    private String detailedAddress;

    private String entrancePassword; // 현관 비밀번호

    @Setter
    private boolean isMainAddress;

    private double latitude;

    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateAddress(AddressUpdateDTO addressUpdateDTO) {
        this.addressName = addressUpdateDTO.getAddressName();
        this.address = addressUpdateDTO.getAddress();
        this.detailedAddress = addressUpdateDTO.getDetailedAddress();
        this.entrancePassword = addressUpdateDTO.getEntrancePassword();
        this.latitude = addressUpdateDTO.getLatitude();
        this.longitude = addressUpdateDTO.getLongitude();
    }
}
