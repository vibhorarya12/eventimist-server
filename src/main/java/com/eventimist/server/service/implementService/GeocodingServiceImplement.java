package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.geocoding.GeocodingResponseDTO;
import com.eventimist.server.service.GeocodingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImplement
        implements GeocodingService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${maps.key}")
    private String mapsApiKey;

    @Override
    public GeocodingResponseDTO geocode(
            String address
    ) {

        try {

            String encodedAddress =
                    URLEncoder.encode(
                            address,
                            StandardCharsets.UTF_8
                    );

            String url =
                    "https://maps.googleapis.com/maps/api/geocode/json"
                            + "?address=" + encodedAddress
                            + "&key=" + mapsApiKey;

            String response =
                    restTemplate.getForObject(
                            url,
                            String.class
                    );

            JsonNode root =
                    objectMapper.readTree(
                            response
                    );

            JsonNode results =
                    root.path("results");

            if (results.isEmpty()) {
                return null;
            }

            JsonNode location =
                    results.get(0)
                            .path("geometry")
                            .path("location");

            GeocodingResponseDTO dto =
                    new GeocodingResponseDTO();

            dto.setLatitude(
                    location.path("lat").asDouble()
            );

            dto.setLongitude(
                    location.path("lng").asDouble()
            );

            return dto;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to geocode address: " + address,
                    e
            );
        }
    }
}