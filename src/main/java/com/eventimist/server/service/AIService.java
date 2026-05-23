package com.eventimist.server.service;

import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;

public interface AIService {

    AIEventDraftResponseDTO generateEventDraft(
            GenerateEventDraftRequestDTO requestDTO
    );

}