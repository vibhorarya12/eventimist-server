package com.eventimist.server.repository;

import java.time.LocalDateTime;

public interface NearbyEventProjection {

    Long getId();
    String getTitle();
    String getDescription();
    String getCategory();

    java.time.LocalDateTime getStartTime();
    String getTimezone();

    String getMode();
    String getVenue();

    Double getLatitude();
    Double getLongitude();

    String getCoverImage();

    Double getDistance();
    String getSlug();
    String getOrganizerName();
    String getOrganizerImage();
}