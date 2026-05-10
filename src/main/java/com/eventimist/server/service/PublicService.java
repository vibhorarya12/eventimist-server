package com.eventimist.server.service;

import com.eventimist.server.dto.publicDTO.DiscoverEventResponseDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.ViewEventResponseDTO;

import java.util.List;

public interface PublicService {

    public List<DiscoverEventResponseDTO> discoverEvents(DiscoverEventsRequestDTO dto);
    public ViewEventResponseDTO getEvent(String slug);


}
