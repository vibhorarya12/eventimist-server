package com.eventimist.server.dto.scrape;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScrapedEventDTO {

    private String title;

    private String description;

    private String coverImage;

    private String sourceUrl;

    private String sourceEventId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String timezone;

    private Boolean free;

    private ScrapedOrganizerDTO organizer;

    private ScrapedVenueDTO venue;
}