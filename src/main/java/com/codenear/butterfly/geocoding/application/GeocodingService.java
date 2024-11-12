package com.codenear.butterfly.geocoding.application;

import com.codenear.butterfly.geocoding.domain.Address;
import com.codenear.butterfly.geocoding.domain.DepartureAddress;
import com.codenear.butterfly.geocoding.domain.dto.GeocodingResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private static final DepartureAddress DEFAULT_DEPARTURE = DepartureAddress.BASIC;
    private static final double DEFAULT_DISTANCE = 0.0;

    private final GeocodingClient geocodingClient;

    public int fetchDistance(String arrivalAddress) {
        Address address = createAddress(arrivalAddress);
        Optional<GeocodingResponse> response = geocodingClient.loadGeocoding(address);
        double distance = extractDistance(response);
        return roundDownDistance(distance);
    }

    private Address createAddress(String arrivalAddress) {
        return new Address(arrivalAddress, DEFAULT_DEPARTURE);
    }

    private int roundDownDistance(Double distance) {
        return (int) Math.floor(distance);
    }

    private double extractDistance(Optional<GeocodingResponse> response) {
        return response.map(geocodingResponse -> geocodingResponse.getAddresses().get(0).getDistance())
                .orElse(DEFAULT_DISTANCE);
    }
}
