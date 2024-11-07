package com.codenear.butterfly.geocoding.application;

import com.codenear.butterfly.geocoding.domain.Address;
import com.codenear.butterfly.geocoding.domain.dto.GeocodingResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class GeocodingClient {
    private static final String API_KEY_ID_HEADER = "x-ncp-apigw-api-key-id";
    private static final String API_KEY_HEADER = "x-ncp-apigw-api-key";

    private final RestTemplate restTemplate;
    private final GeocodingValidator geocodingValidator;

    @Value("${naver.client.id}")
    private String id;

    @Value("${naver.client.secret.key}")
    private String secretKey;

    @Value("${naver.client.url}")
    private String url;

    public GeocodingResponse loadGeocoding(Address address) {
        URI uri = createRequestUri(address);
        HttpEntity<String> entity = createHttpEntity();
        ResponseEntity<GeocodingResponse> response = sendRequest(uri, entity);

        geocodingValidator.validateResponse(response);

        return response.getBody();
    }

    private ResponseEntity<GeocodingResponse> sendRequest(URI query, HttpEntity<String> entity) {
        return restTemplate.exchange(
                query,
                HttpMethod.GET,
                entity,
                GeocodingResponse.class
        );
    }

    private URI createRequestUri(Address address) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", address.getArrivalAddress())
                .queryParam("coordinate", address.getDepartureAddress().getAddress())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
    }

    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_ID_HEADER, id);
        headers.set(API_KEY_HEADER, secretKey);
        return new HttpEntity<>(headers);
    }
}
