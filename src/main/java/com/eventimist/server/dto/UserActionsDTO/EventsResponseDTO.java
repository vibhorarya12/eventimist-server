package com.eventimist.server.dto.UserActionsDTO;

import com.eventimist.server.entities.OrganizerEntity;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.util.Date;
import java.util.List;

@Data
public class EventsResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String description;
    private Date date;
    private String venue;
    private Long attendance;
    private List <String> images;
    private Long organizerId;
    private String organizerName;
    private String organizerProfilePic;
    private Double latitude;
    private Double longitude;
}