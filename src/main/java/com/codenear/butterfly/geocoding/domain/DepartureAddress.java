package com.codenear.butterfly.geocoding.domain;

public enum DepartureAddress {
    BASIC("127.92251", "37.30574");

    private static final String POS_DELIMITER = ",";

    private final String longitude;
    private final String latitude;

    DepartureAddress(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return longitude + POS_DELIMITER + latitude;
    }
}
