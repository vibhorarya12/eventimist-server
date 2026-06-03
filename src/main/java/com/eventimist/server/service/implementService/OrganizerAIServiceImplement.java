package com.eventimist.server.service.implementService;

import com.eventimist.server.ai.OrganizerAITools;
import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.organizerActionsDTO.OrganizerSubscriptionResponseDTO;
import com.eventimist.server.enums.AIIntent;
import com.eventimist.server.enums.EventStatus;
import com.eventimist.server.service.OrganizerAIService;
import com.eventimist.server.service.OrganizerActionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.eventimist.server.ai.dto.AIEventSummaryDTO;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizerAIServiceImplement implements OrganizerAIService {

    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    private final OrganizerAITools organizerAITools;

    private final OrganizerActionsService organizerActionsService;



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

        try {

            AIIntent intent = determineIntent(prompt);

            switch (intent) {

                case DRAFT_EVENTS -> {

                    List<AIEventSummaryDTO> events =
                            organizerActionsService.getEventsByStatus(
                                    EventStatus.DRAFT
                            );

                    return AIChatResponseDTO.builder()
                            .type("DRAFT_EVENTS")
                            .message("Found " + events.size() + " drafted events")
                            .data(events)
                            .build();
                }

                case PUBLISHED_EVENTS -> {

                    List<AIEventSummaryDTO> events =
                            organizerActionsService.getEventsByStatus(
                                    EventStatus.PUBLISHED
                            );

                    return AIChatResponseDTO.builder()
                            .type("PUBLISHED_EVENTS")
                            .message("Found " + events.size() + " published events")
                            .data(events)
                            .build();
                }
                case SUBSCRIPTION_INFO -> {

                    OrganizerSubscriptionResponseDTO subscription =
                            organizerActionsService.getOrganizerSubscription();

                    return AIChatResponseDTO.builder()
                            .type("SUBSCRIPTION_INFO")
                            .message("Subscription details retrieved.")
                            .data(subscription)
                            .build();
                }
                default -> {

                    return AIChatResponseDTO.builder()
                            .type("TEXT")
                            .message("""
                I can help you manage your events.

                Try asking:
                • List my drafted events
                • Show my published events
                • Show my event analytics
                • How much revenue have I earned?
                """)
                            .data(null)
                            .build();
                }
            }

        } catch (Exception ex) {

            log.error("AI chat failed", ex);

            return AIChatResponseDTO.builder()
                    .type("ERROR")
                    .message("Something went wrong while processing your request.")
                    .data(null)
                    .build();
        }
    }


    private AIIntent determineIntent(String prompt) {

        String systemPrompt = """
You are an intent classification engine.

Your ONLY job is to classify the user's request.

Valid intents:

- DRAFT_EVENTS
- PUBLISHED_EVENTS
- SUBSCRIPTION_INFO
- REVENUE_SUMMARY
- EVENT_ANALYTICS
- GENERAL_CHAT
- UNKNOWN

Classification Rules:

DRAFT_EVENTS:
- list my drafts
- show drafted events
- show my draft events
- list draft events

PUBLISHED_EVENTS:
- list published events
- show my published events
- show live events
- list live events

SUBSCRIPTION_INFO:
- what plan am i on
- show my subscription
- how many ai credits do i have left
- how many credits remain
- show my plan
- show ai usage
- how many credits are remaining

REVENUE_SUMMARY:
- how much revenue have i earned
- show my earnings
- show my revenue

EVENT_ANALYTICS:
- show event analytics
- show event performance
- which event performed best

GENERAL_CHAT:
- hello
- hi
- how are you
- good morning
- who are you

UNKNOWN:
- anything that does not clearly match the above intents

IMPORTANT:
- Return ONLY one intent name.
- Do not explain your answer.
- Do not return JSON.
- Do not return markdown.
- If unsure, return UNKNOWN.

Examples:

User: list my drafted events
DRAFT_EVENTS

User: show published events
PUBLISHED_EVENTS

User: what plan am i on
SUBSCRIPTION_INFO

User: how many ai credits do i have left
SUBSCRIPTION_INFO

User: show my subscription
SUBSCRIPTION_INFO

User: how much revenue have i earned
REVENUE_SUMMARY

User: show event analytics
EVENT_ANALYTICS

User: hello
GENERAL_CHAT

User: how are you
GENERAL_CHAT

User: tell me a joke
UNKNOWN
""";

        String result = chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();
        log.info("Intent classifier raw response: [{}]", result);
        try {
            return AIIntent.valueOf(result.trim());
        } catch (Exception e) {
            return AIIntent.UNKNOWN;
        }
    }
}