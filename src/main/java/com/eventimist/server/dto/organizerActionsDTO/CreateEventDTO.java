package com.eventimist.server.dto.organizerActionsDTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class CreateEventDTO {
    private String title;
    private String type;
    private String description;
    private String date;
    private String venue;
    private List<String> tags;
    private String latitude;
    private String longitude;
    private Long attendance;
    private Long organizerId;
}
