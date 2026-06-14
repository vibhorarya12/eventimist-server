package com.eventimist.server.dto.publicDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DiscoverEventsRequestDTO {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @DecimalMin(value = "1.0", message = "Radius must be at least 1 km")
    @DecimalMax(value = "100.0", message = "Radius cannot exceed 100 km")
    private Double radius = 10.0;

    @Min(value = 0, message = "Page cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    private Integer limit = 20;
}