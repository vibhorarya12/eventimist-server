package com.eventimist.server.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateEventDraftRequestDTO {

    @NotBlank(message = "Prompt is required")
    private String prompt;

}