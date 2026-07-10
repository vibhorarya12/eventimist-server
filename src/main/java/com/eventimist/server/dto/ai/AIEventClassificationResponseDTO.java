package com.eventimist.server.dto.ai;

import com.eventimist.server.enums.EventCategory;
import lombok.Data;

import java.util.List;

@Data
public class AIEventClassificationResponseDTO {

    private EventCategory category;

    private List<String> tags;

}