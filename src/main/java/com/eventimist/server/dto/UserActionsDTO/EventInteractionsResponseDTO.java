// EventInteractionsResponseDTO.java

package com.eventimist.server.dto.UserActionsDTO;

import lombok.Data;

import java.util.List;

@Data
public class EventInteractionsResponseDTO {

    private List<Long> rsvpEventIds;

}