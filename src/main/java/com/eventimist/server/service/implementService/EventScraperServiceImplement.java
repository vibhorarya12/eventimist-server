package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.geocoding.GeocodingResponseDTO;
import com.eventimist.server.service.EventScraperService;
import com.eventimist.server.dto.scrape.*;
import com.eventimist.server.service.GeocodingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventScraperServiceImplement implements EventScraperService {

    private final ObjectMapper objectMapper;
    private final GeocodingService geocodingService;


    @Override
    public ScrapedEventDTO scrape(
            String url
    ) {

        try {

            Document document =
                    Jsoup.connect(url)
                            .userAgent(
                                    "Mozilla/5.0"
                            )
                            .get();

            Element nextData =
                    document.getElementById(
                            "__NEXT_DATA__"
                    );

            if (nextData == null) {

                throw new RuntimeException(
                        "__NEXT_DATA__ not found"
                );
            }

            JsonNode root =
                    objectMapper.readTree(
                            nextData.html()
                    );
//            System.out.println(root.toPrettyString());
            JsonNode context =
                    root.path("props")
                            .path("pageProps")
                            .path("context");

            JsonNode basicInfo =
                    context.path("basicInfo");

            log.info(
                    basicInfo.toPrettyString()
            );
//            System.out.println(
//                    basicInfo.path("startDate").toPrettyString()
//            );
//
//            System.out.println(
//                    basicInfo.path("endDate").toPrettyString()
//            );
            System.out.println(
                    context.path("basicInfo").toPrettyString()
            );

            return mapToScrapedEvent(
                    basicInfo,
                    context,
                    url
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to scrape Eventbrite event",
                    e
            );
        }
    }

    private ScrapedEventDTO mapToScrapedEvent(
            JsonNode event,
            JsonNode context,
            String sourceUrl
    ) {

        ScrapedEventDTO dto =
                new ScrapedEventDTO();

        dto.setTitle(
                event.path("name").asText()
        );

        dto.setDescription(
                event.path("summary").asText()
        );

        dto.setSourceUrl(
                sourceUrl
        );

        dto.setSourceEventId(
                event.path("id").asText()
        );

        dto.setCoverImage(
                context.path("gallery")
                        .path("images")
                        .get(0)
                        .path("url")
                        .asText(null)
        );

        dto.setTimezone(
                event.path("startDate")
                        .path("timezone")
                        .asText()
        );

        dto.setFree(
                event.path("isFree")
                        .asBoolean()
        );

        String start =
                event.path("startDate")
                        .path("local")
                        .asText(null);

        if (start != null && !start.isBlank()) {
            dto.setStartTime(
                    LocalDateTime.parse(start)
            );
        }

        String End = event.path("endDate")
                .path("local")
                .asText(null);

        if(End != null && !End.isBlank()){
            dto.setEndTime(
                    LocalDateTime.parse(
                            event.path("endDate")
                                    .path("local")
                                    .asText()
                    )
            );
        }

        dto.setOrganizer(
                extractOrganizer(event)
        );

        ScrapedVenueDTO venue =
                extractVenue(event);

        String address =
                venue.getVenueName()
                        + ", "
                        + venue.getCity()
                        + ", "
                        + venue.getState()
                        + ", "
                        + venue.getCountry();

        GeocodingResponseDTO coordinates =
                geocodingService.geocode(address);

        if (coordinates != null) {

            venue.setLatitude(
                    coordinates.getLatitude()
            );

            venue.setLongitude(
                    coordinates.getLongitude()
            );
        }

        dto.setVenue(venue);

        return dto;
    }

    private ScrapedOrganizerDTO extractOrganizer(
            JsonNode event
    ) {

        JsonNode organizer =
                event.path("organizer");

        ScrapedOrganizerDTO dto =
                new ScrapedOrganizerDTO();

        dto.setName(
                organizer.path("name")
                        .asText()
        );

        dto.setBio(
                organizer.path("description")
                        .asText(null)
        );

        dto.setProfileImage(
                organizer.path("image")
                        .path("url")
                        .asText(null)
        );

        return dto;
    }

    private ScrapedVenueDTO extractVenue(
            JsonNode event
    ) {

        JsonNode venue =
                event.path("venue");

        ScrapedVenueDTO dto =
                new ScrapedVenueDTO();

        dto.setVenueName(
                venue.path("name")
                        .asText()
        );

        dto.setAddress(
                venue.path("address")
                        .path("localized_address_display")
                        .asText()
        );

        dto.setCity(
                venue.path("address")
                        .path("city")
                        .asText()
        );

        dto.setState(
                venue.path("address")
                        .path("region")
                        .asText()
        );

        dto.setCountry(
                venue.path("address")
                        .path("country")
                        .asText()
        );

        dto.setLatitude(
                venue.path("latitude")
                        .asDouble()
        );

        dto.setLongitude(
                venue.path("longitude")
                        .asDouble()
        );

        return dto;
    }



}
