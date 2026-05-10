package com.eventimist.server.dto.publicDTO;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscoverEventResponseDTO {

    private Long id;
    private String title;
    private String description;

    private EventCategory category;

    private LocalDateTime startTime;
    private String timezone;

    private EventMode mode;
    private String venue;

    private Double latitude;
    private Double longitude;

    private String coverImage;

    private Double distance;

    private String slug;
    private String organizerName;
    private String organizerImage;
}