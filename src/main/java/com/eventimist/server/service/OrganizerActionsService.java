package com.eventimist.server.service;

import com.eventimist.server.dto.ai.AIEventDraftResponseDTO;
import com.eventimist.server.dto.ai.GenerateEventDraftRequestDTO;
import com.eventimist.server.dto.organizerActionsDTO.*;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.enums.EventStatus;
import org.springframework.web.multipart.MultipartFile;
import com.eventimist.server.ai.dto.AIEventSummaryDTO;

import java.util.List;

public interface OrganizerActionsService {
        EventEntity createEvent(CreateEventDTO createEventDTO , MultipartFile [] files);
        List<GetEventsResponseDTO> getEventsByOrganizerId();
        UpdateProfileDTO updateProfileInfo (UpdateProfileDTO updateProfileDTO);
        String UpdateImage (MultipartFile file , String type);
        public EventEntity updateEvent(Long id, EditEventDTO dto);
        public void publishEvent(Long eventId);
//        AIEventDraftResponseDTO organizerAIEventsDraft (GenerateEventDraftRequestDTO dto);
        OrganizerSubscriptionResponseDTO getOrganizerSubscription();
        List<AIEventSummaryDTO> getEventsByStatus (EventStatus status);




}
