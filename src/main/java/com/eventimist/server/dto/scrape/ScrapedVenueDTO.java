package com.eventimist.server.dto.scrape;

import lombok.Data;

@Data
public class ScrapedVenueDTO {

    private String venueName;

    private String address;

    private String city;

    private String state;

    private String country;

    private Double latitude;

    private Double longitude;
}