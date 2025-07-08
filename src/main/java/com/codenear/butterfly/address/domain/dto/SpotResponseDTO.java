package com.codenear.butterfly.address.domain.dto;

import com.codenear.butterfly.address.domain.Spot;

public record SpotResponseDTO(String addressName,
                              Double latitude,
                              Double longitude) {
    public static SpotResponseDTO fromSpot(Spot spot) {
        return new SpotResponseDTO(spot.getAddressName(), spot.getLatitude(), spot.getLongitude());
    }
}
