package com.eventimist.server.service;

import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftResponseDTO;

public interface OrganizerAIService {

    GenerateEventDraftResponseDTO generateEventDraft(
            GenerateEventDraftRequestDTO requestDTO
    );
    AIChatResponseDTO chat(String prompt);

}