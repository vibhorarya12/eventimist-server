package com.eventimist.server.service.implementService;

import com.eventimist.server.ai.SystemPrompts;
import com.eventimist.server.dto.ai.AIEventClassificationResponseDTO;
import com.eventimist.server.service.AIEventClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIEventClassificationServiceImplement
        implements AIEventClassificationService {

    private final ChatClient chatClient;



    @Override
    public AIEventClassificationResponseDTO classify(
            String title,
            String description
    ) {



        try {

            return chatClient.prompt()
                    .system(SystemPrompts.EVENT_CLASSIFICATION)
                    .user("""
                Title:
                %s

                Description:
                %s
                """.formatted(title, description))
                    .call()
                    .entity(AIEventClassificationResponseDTO.class);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to classify event",
                    e
            );

        }

    }

}