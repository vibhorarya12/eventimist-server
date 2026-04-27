package com.eventimist.server.dto.organizerActionsDTO;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEventDTO {

    // ---------------- Core Info ----------------
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Event description is required")
    private String description;

    @NotNull(message = "Category is required")
    private EventCategory category;

    // ---------------- Time ----------------
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    // ---------------- Location & Mode ----------------
    @NotNull(message = "Event mode is required")
    private EventMode mode;

    private String venue;        // required if OFFLINE / HYBRID
    private String onlineLink;   // required if ONLINE / HYBRID

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    // ---------------- Media ----------------
    private String coverImage;

    private List<String> images;

    // ---------------- Discovery ----------------
    @NotEmpty(message = "At least one event tag is required")
    private List<String> tags;

    // ---------------- Ticketing ----------------
    private Integer capacity;

    private BigDecimal ticketPrice;

//    @NotNull(message = "isFree flag is required")
    private Boolean isFree;
}