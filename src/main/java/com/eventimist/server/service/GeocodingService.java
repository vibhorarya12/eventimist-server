package com.eventimist.server.service;

import com.eventimist.server.dto.geocoding.GeocodingResponseDTO;

public interface GeocodingService {

    GeocodingResponseDTO geocode(
            String address
    );
}