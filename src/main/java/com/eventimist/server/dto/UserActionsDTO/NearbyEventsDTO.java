package com.eventimist.server.dto.UserActionsDTO;

import lombok.Data;

@Data
public class NearbyEventsDTO {
    private double latitude;
    private double longitude;
    private double radiusKm;
}
