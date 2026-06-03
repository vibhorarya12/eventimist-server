package com.eventimist.server.service.implementService;

import com.eventimist.server.ai.OrganizerAITools;
import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIServiceImplement implements AIService {

    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    private final OrganizerAITools organizerAITools;

    @Override
    public AIEventDraftResponseDTO generateEventDraft(
            GenerateEventDraftRequestDTO requestDTO
    ) {

        String systemPrompt = """
Generate event draft JSON only.

An event is a meetup, workshop, webinar, conference, hackathon, bootcamp, festival, networking session, competition, or community gathering attended physically or online.

Rules:
- Return ONLY valid JSON.
- Never add explanations.
- Never invent missing information.
- Never invent dates, times, URLs, modes, locations, or categories.
- Only generate fields clearly requested or inferable from the prompt.
- Return empty values for unnecessary or unknown fields.
- Generate memorable and engaging event titles suitable for marketing posters and event listings.
- Generate exactly 5 relevant lowercase tags only if tags are meaningfully inferable.
- Description should be engaging, professional, and 3-5 sentences only if description is requested or inferable.
- If startTime exists -> endTime = +3hrs.
- If no date/time provided -> empty startTime/endTime.
- Detect mode only if explicitly mentioned:
  ONLINE, OFFLINE, HYBRID.
- If mode absent -> empty mode.
- onlineLink only if explicitly provided.
- Use ISO datetime format.
- If the prompt describes an event concept or idea, generate a complete event draft with title, description, category, and tags whenever reasonably inferable.

Allowed categories:
MUSIC,TECH,BUSINESS,ART,SPORTS,EDUCATION,HEALTH,FOOD,NETWORKING,OTHER

JSON:
{
"title":"",
"description":"",
"category":"",
"tags":[],
"startTime":"",
"endTime":"",
"mode":"",
"onlineLink":""
}
""";
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(requestDTO.getPrompt())
                .call()
                .content();

        try {
            return objectMapper.readValue(
                    response,
                    AIEventDraftResponseDTO.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response");
        }
    }

    @Override
    public AIChatResponseDTO chat(String prompt) {

        return chatClient.prompt()
                .user(prompt)
                .tools(organizerAITools)
                .call()
                .entity(AIChatResponseDTO.class);
    }




}