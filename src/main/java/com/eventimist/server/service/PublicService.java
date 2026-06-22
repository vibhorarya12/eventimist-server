package com.eventimist.server.service;

import com.eventimist.server.dto.publicDTO.*;

import java.util.List;

public interface PublicService {

//    public List<DiscoverEventResponseDTO> discoverEvents(DiscoverEventsRequestDTO dto);
    public ViewEventResponseDTO getEvent(String slug);
    public DiscoverEventsResponseDTO discoverEvents(
            DiscoverEventsRequestDTO dto
    );
    DiscoverEventsResponseDTO discoverEventsByPrompt(
            AIDiscoverEventsRequestDTO requestDTO
    );

}
