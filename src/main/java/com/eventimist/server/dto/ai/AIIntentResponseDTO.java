package com.eventimist.server.dto.ai;

import com.eventimist.server.enums.AIIntent;
import lombok.Data;


@Data
public class AIIntentResponseDTO {
    private AIIntent intent;

}
