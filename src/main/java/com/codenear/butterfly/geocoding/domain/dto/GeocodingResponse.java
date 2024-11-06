package com.codenear.butterfly.geocoding.domain.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class GeocodingResponse {
    private Meta meta;
    private List<Address> addresses;

    @Getter
    public static class Meta {
        private int totalCount;
    }

    @Getter
    public static class Address {
        private double distance;
    }
}
