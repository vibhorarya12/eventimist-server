package com.eventimist.server.dto.publicDTO;

import com.eventimist.server.enums.EventCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiscoverEventsRequestDTO {

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private Double radius;

    private EventCategory category;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 0)
    private Integer page = 0;

    @Min(value = 1)
    @Max(value = 100)
    private Integer limit = 20;
}