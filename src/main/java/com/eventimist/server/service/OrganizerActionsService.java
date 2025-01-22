package com.eventimist.server.service;

import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.dto.organizerActionsDTO.GetEventsResponseDTO;
import com.eventimist.server.entities.EventEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizerActionsService {
        EventEntity createEvent(CreateEventDTO createEventDTO , MultipartFile [] files);
        List<GetEventsResponseDTO> getEventsByOrganizerId();
}
