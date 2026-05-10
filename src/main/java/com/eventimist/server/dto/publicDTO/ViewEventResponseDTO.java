package com.eventimist.server.dto.publicDTO;

import com.eventimist.server.enums.EventCategory;
import com.eventimist.server.enums.EventMode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ViewEventResponseDTO {

    // ---------------- Basic ----------------
    private Long id;
    private String slug;

    private String title;
    private String description;

    private EventCategory category;

    // ---------------- Time ----------------
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String timezone;

    // ---------------- Mode & Location ----------------
    private EventMode mode;

    private String venue;

    private Double latitude;
    private Double longitude;

    // ---------------- Media ----------------
    private String coverImage;

    private List<String> images;

    // ---------------- Organizer ----------------
    private Long organizerId;

    private String organizerName;

    private String organizerImage;

    // ---------------- Engagement ----------------
    private Long attendance;

    private Long rsvpCount;

    // ---------------- Ticketing ----------------
    private Boolean isFree;

    private BigDecimal ticketPrice;

    // ---------------- Discovery ----------------
    private List<String> tags;
}