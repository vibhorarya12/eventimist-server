package com.eventimist.server.service;

import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;

import java.util.List;

public interface UserActionsService {

    void bookmarkEvents (Long userId ,  Long eventId);
    void attendEvents (Long userId , Long eventId);
    List<EventsResponseDTO> getBookmarkedEvents (Long userId );
    List<EventsResponseDTO>getAttendingEvents (Long userId);
}
