package com.eventimist.server.dto.UserActionsDTO;

import lombok.Data;

import java.util.Date;

@Data
public class EventsResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String description;
    private Date date;
    private String venue;
    private Long attendance;
}