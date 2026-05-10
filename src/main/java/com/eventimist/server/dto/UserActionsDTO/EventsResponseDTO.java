package com.eventimist.server.dto.UserActionsDTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EventsResponseDTO {

    private Long id;

    private String title;

    // category/type of event
    private String type;

    private String description;

    // event start date/time
    private Date date;

    private String venue;

    private Long attendance;

    private List<String> images;

    // organizer info
    private Long organizerId;

    private String organizerName;

    private String organizerProfilePic;

    // location
    private Double latitude;

    private Double longitude;

    // extra useful fields
    private String coverImage;

    private Long rsvpCount;

    private Boolean isFree;
}