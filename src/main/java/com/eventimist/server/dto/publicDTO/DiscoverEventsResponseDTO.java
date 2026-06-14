package com.eventimist.server.dto.publicDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoverEventsResponseDTO {

    private List<DiscoverEventResponseDTO> events;

    private Integer page;

    private Integer limit;

    private Long totalEvents;

    private Boolean hasMore;
}