package com.eventimist.server.dto.organizerActionsDTO;

import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.util.Date;
import java.util.List;

@Data
public class GetEventsResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String description;
    private Date date;
    private String venue;
    private List<String> tags;
    private Double latitude;
    private Double longitude;
    private List<String> images;
    private Long attendance;

}
