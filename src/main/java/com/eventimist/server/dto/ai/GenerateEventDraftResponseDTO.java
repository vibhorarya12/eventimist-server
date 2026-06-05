package com.eventimist.server.dto.ai;

import com.eventimist.server.dto.organizerActionsDTO.OrganizerSubscriptionResponseDTO;
import com.eventimist.server.entities.OrganizerSubscriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateEventDraftResponseDTO {

    private AIEventDraftResponseDTO draft;

    private OrganizerSubscriptionResponseDTO subscription;
}
