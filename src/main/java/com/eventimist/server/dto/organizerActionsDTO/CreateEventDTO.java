package com.eventimist.server.dto.organizerActionsDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateEventDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Event type is required")
    private String type;

    @NotBlank(message = "Event description is required")
    private String description;

    @NotBlank(message = "Event date is required")
    private String date;

    @NotBlank(message = "Event venue is required")
    private String venue;

    @NotEmpty(message = "At least one event tag is required")
    private List<String> tags;

    @NotNull(message = "Location coordinates: latitude is required")
    private Double latitude;

    @NotNull(message = "Location coordinates: longitude is required")
    private Double longitude;


}
