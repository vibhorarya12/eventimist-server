package com.eventimist.server.service.implementService;

import com.eventimist.server.ai.OrganizerAITools;
import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftResponseDTO;
import com.eventimist.server.dto.organizerActionsDTO.OrganizerSubscriptionResponseDTO;
import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import com.eventimist.server.enums.AIIntent;
import com.eventimist.server.enums.EventStatus;
import com.eventimist.server.exceptions.BadRequestException;
import com.eventimist.server.exceptions.EntityNotFoundException;
import com.eventimist.server.repository.OrganizerSubscriptionRepository;
import com.eventimist.server.service.OrganizerAIService;
import com.eventimist.server.service.OrganizerActionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.eventimist.server.ai.dto.AIEventSummaryDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import com.eventimist.server.ai.SystemPrompts;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizerAIServiceImplement implements OrganizerAIService {

    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    private final OrganizerAITools organizerAITools;

    private final OrganizerActionsService organizerActionsService;

    private  final OrganizerSubscriptionRepository organizerSubscriptionRepository;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Long getUserId() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new IllegalStateException("Principal is not of type Long");
    }

    @Override
    @Transactional
    public GenerateEventDraftResponseDTO generateEventDraft(
            GenerateEventDraftRequestDTO requestDTO
    ) {

        OrganizerSubscriptionEntity subscription =
                organizerSubscriptionRepository
                        .findByOrganizerId(getUserId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Subscription not found"
                                ));

        if (subscription.getAiCreditsRemaining() <= 0) {
            throw new BadRequestException(
                    "No AI credits remaining."
            );
        }


        String aiResponse;

        try {

            aiResponse = chatClient.prompt()
                    .system(SystemPrompts.EVENT_DRAFT_GENERATION)
                    .user(requestDTO.getPrompt())
                    .call()
                    .content();

        } catch (Exception e) {

            log.error(
                    "AI generation failed for organizer {}",
                    getUserId(),
                    e
            );

            throw new RuntimeException(
                    "Unable to generate event draft at the moment."
            );
        }

        try {

            AIEventDraftResponseDTO draft =
                    objectMapper.readValue(
                            aiResponse,
                            AIEventDraftResponseDTO.class
                    );

            subscription.setAiCreditsRemaining(
                    subscription.getAiCreditsRemaining() - 1
            );

            organizerSubscriptionRepository.save(subscription);

            OrganizerSubscriptionResponseDTO updatedSubscription =
                    organizerActionsService.getOrganizerSubscription();

            return GenerateEventDraftResponseDTO.builder()
                    .draft(draft)
                    .subscription(updatedSubscription)
                    .build();

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

            log.error(
                    "Failed to parse AI response. Response={}",
                    aiResponse,
                    e
            );

            throw new RuntimeException(
                    "AI returned an invalid response."
            );
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

                case GENERAL_CHAT -> {

                    return AIChatResponseDTO.builder()
                            .type("TEXT")
                            .message("""
                    Hi! I'm Eventimist AI.

                    I can help you:
                    • List drafted events
                    • Show published events
                    • View subscription details
                    • Check AI credits

                    Try asking me something about your events.
                    """)
                            .data(null)
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
        String p = prompt.toLowerCase().trim();

        if (
                p.equals("hi") ||
                        p.equals("hello") ||
                        p.equals("hey") ||
                        p.equals("thanks")
        ) {
            log.info("cached chat called");
            return AIIntent.GENERAL_CHAT;
        }


        String result = chatClient.prompt()
                .system(SystemPrompts.INTENT_CLASSIFIER)
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