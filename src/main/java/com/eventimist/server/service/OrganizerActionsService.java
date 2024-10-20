package com.eventimist.server.service;

import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.entities.EventEntity;

import java.util.List;

public interface OrganizerActionsService {
        EventEntity createEvent(CreateEventDTO createEventDTO);
        List<EventEntity> getEventsByOrganizerId(Long organizerId);
}
