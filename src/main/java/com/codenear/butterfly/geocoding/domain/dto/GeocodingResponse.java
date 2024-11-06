package com.codenear.butterfly.geocoding.domain.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class GeocodingResponse {
    private String status;
    private Meta meta;
    private List<Address> addresses;
    private String errorMessage;

    @Getter
    public static class Meta {
        private int totalCount;
    }

    @Getter
    public static class Address {
        private double distance;
    }
}
