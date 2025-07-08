package com.codenear.butterfly.address.domain;

import com.codenear.butterfly.address.domain.dto.SpotResponseDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressName;

    private Double longitude;

    private Double latitude;

    public Spot(SpotResponseDTO spotResponseDTO) {
        this.addressName = spotResponseDTO.addressName();
        this.longitude = spotResponseDTO.longitude();
        this.latitude = spotResponseDTO.latitude();
    }
}
