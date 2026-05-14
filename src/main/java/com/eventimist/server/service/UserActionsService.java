package com.eventimist.server.service;

import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;

import java.util.List;

public interface UserActionsService {

    void bookmarkEvents (Long eventId);
    void rsvpEvent(Long eventId);
    void removeRsvp(Long eventId);
    List<EventsResponseDTO>  getRsvpEvents ();
    List<EventsResponseDTO> getBookmarkedEvents (Long userId );
    List<EventsResponseDTO>getAttendingEvents (Long userId);
//    List<EventsResponseDTO>getNearbyEvents(double latitude, double longitude, double radiusKm);
}
