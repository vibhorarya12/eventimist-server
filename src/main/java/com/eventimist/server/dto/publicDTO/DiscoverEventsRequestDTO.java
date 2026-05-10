package com.eventimist.server.dto.publicDTO;

import lombok.Data;

@Data
public class DiscoverEventsRequestDTO {

    private Double latitude;
    private Double longitude;

    // in kilometers
    private Double radius;
}