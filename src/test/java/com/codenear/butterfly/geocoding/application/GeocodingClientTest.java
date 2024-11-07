package com.codenear.butterfly.geocoding.application;

import static com.codenear.butterfly.geocoding.domain.DepartureAddress.BASIC;

import com.codenear.butterfly.geocoding.domain.Address;
import com.codenear.butterfly.geocoding.domain.dto.GeocodingResponse;
import com.codenear.butterfly.geocoding.exception.GeocodingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class GeocodingClientTest {

    private static final Logger log = LoggerFactory.getLogger(GeocodingClientTest.class);
    @Autowired
    private GeocodingClient geocodingClient;

    @Test
    void 정상_주소의_거리를_반환한다() {
        // given
        String arrivalAddress = "강원특별자치도 강릉시 죽헌길 7";
        Address address = new Address(arrivalAddress, BASIC);

        // when
        GeocodingResponse response = geocodingClient.loadGeocoding(address);

        // then
        Assertions.assertThat(response.getAddresses().get(0).getDistance())
                .isNotNull();
    }

    @Test
    void 비정상_주소는_예외가_발생한다() {
        // given
        String arrivalAddress = "제주도 강원도 경기도";
        Address address = new Address(arrivalAddress, BASIC);

        // when & then
        Assertions.assertThatThrownBy(() -> geocodingClient.loadGeocoding(address))
                .isInstanceOf(GeocodingException.class);
    }

    @Test
    void 주소가_NULL인_경우_예외가_발생한다() {
        // given
        Address address = new Address(null, BASIC);

        // when & then
        Assertions.assertThatThrownBy(() -> geocodingClient.loadGeocoding(address))
                .isInstanceOf(GeocodingException.class);
    }
}