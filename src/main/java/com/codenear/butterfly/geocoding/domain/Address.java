package com.codenear.butterfly.geocoding.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Address {
    private String arrivalAddress;
    private DepartureAddress departureAddress;
}
