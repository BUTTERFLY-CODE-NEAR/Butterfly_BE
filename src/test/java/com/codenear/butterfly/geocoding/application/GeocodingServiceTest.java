package com.codenear.butterfly.geocoding.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GeocodingServiceTest {

    @Autowired
    private GeocodingService geocodingService;

    @Test
    void 입력한_주소의_거리를_반환한다() {
        // given
        String arrivalAddress = "강원특별자치도 강릉시 죽헌길 7";

        // when
        int distance = geocodingService.fetchDistance(arrivalAddress);

        // then
        assertThat(distance)
                .isNotZero();
    }
}