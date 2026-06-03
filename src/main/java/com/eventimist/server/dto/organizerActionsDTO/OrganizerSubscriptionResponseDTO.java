package com.eventimist.server.dto.organizerActionsDTO;

import com.eventimist.server.enums.PlanType;
import lombok.Data;

@Data
public class OrganizerSubscriptionResponseDTO {

    private PlanType planType;

    private Integer aiCreditsRemaining;

    private Integer monthlyAiCredits;

    private Integer promptCharacterLimit;

    private Boolean active;
}