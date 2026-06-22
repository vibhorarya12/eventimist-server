package com.eventimist.server.dto.publicDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AIDiscoverEventsRequestDTO {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;
}