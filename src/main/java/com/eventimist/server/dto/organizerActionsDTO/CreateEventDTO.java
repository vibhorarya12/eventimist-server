package com.eventimist.server.dto.organizerActionsDTO;
import lombok.Data;
import java.util.List;

@Data
public class CreateEventDTO {
    private String title;
    private String type;
    private String description;
    private String date;
    private String venue;
    private List<String> tags;
    private Double latitude;
    private Double longitude;

}
