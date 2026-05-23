package com.eventimist.server.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class AIEventDraftResponseDTO {

    private String title;

    private String description;

    private String category;

    private List<String> tags;

    private String startTime;

    private String endTime;

    private String mode;

    private String onlineLink;

}