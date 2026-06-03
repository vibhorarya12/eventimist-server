package com.eventimist.server.ai;

import com.eventimist.server.dto.ai.AIChatResponseDTO;
import com.eventimist.server.enums.EventStatus;
import com.eventimist.server.service.OrganizerActionsService;
import com.eventimist.server.ai.dto.AIEventSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizerAITools {

    private final OrganizerActionsService organizerActionsService;

    @Tool(
            description = "Get all drafted events belonging to the currently authenticated organizer"
    )

    public AIChatResponseDTO getDraftEvents() {

        List<AIEventSummaryDTO> events =
                organizerActionsService.getEventsByStatus(
                        EventStatus.DRAFT
                );

        log.info("Tool returned {} draft events", events.size());
        log.info("Events: {}", events);
        return AIChatResponseDTO.builder()
                .type("DRAFT_EVENTS")
                .message("Found " + events.size() + " drafted events.")
                .data(events)
                .build();
    }

    @Tool(
            description = "Get all published events belonging to the currently authenticated organizer"
    )
    public List<AIEventSummaryDTO> getPublishedEvents() {
        return organizerActionsService.getEventsByStatus(EventStatus.PUBLISHED);
    }
}
