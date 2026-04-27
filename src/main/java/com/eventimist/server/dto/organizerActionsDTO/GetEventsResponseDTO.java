package com.eventimist.server.dto.organizerActionsDTO;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import com.eventimist.server.enums.EventStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetEventsResponseDTO {

    private Long id;
    private String title;
    private String description;

    // ---------------- Classification ----------------
    private EventCategory category;

    // ---------------- Time ----------------
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timezone;

    // ---------------- Location & Mode ----------------
    private EventMode mode;
    private String venue;
    private String onlineLink;

    private Double latitude;
    private Double longitude;

    // ---------------- Media ----------------
    private String coverImage;
    private List<String> images;

    // ---------------- Discovery ----------------
    private List<String> tags;
    private String slug;

    // ---------------- Ticketing ----------------
    private Integer capacity;
    private BigDecimal ticketPrice;
    private Boolean isFree;

    // ---------------- Lifecycle ----------------
    private EventStatus status;

    // ---------------- Engagement ----------------
    private Long attendance;
    private Long rsvpCount;
}