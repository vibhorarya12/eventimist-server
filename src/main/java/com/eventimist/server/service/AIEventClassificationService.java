package com.eventimist.server.service;

import com.eventimist.server.dto.ai.AIEventClassificationResponseDTO;

public interface AIEventClassificationService {

    AIEventClassificationResponseDTO classify(
            String title,
            String description
    );

}