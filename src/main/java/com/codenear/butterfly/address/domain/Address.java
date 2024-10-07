package com.codenear.butterfly.address.domain;

import com.codenear.butterfly.address.domain.dto.AddressUpdateDTO;
import com.codenear.butterfly.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

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

    private boolean isMainAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateAddress(AddressUpdateDTO addressUpdateDTO) {
        this.addressName = addressUpdateDTO.getAddressName();
        this.address = addressUpdateDTO.getAddress();
        this.detailedAddress = addressUpdateDTO.getDetailedAddress();
        this.entrancePassword = addressUpdateDTO.getEntrancePassword();
    }
}
