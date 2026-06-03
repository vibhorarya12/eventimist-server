package com.eventimist.server.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatResponseDTO {

    /**
     * Human-readable AI response.
     */
    private String message;

    /**
     * Response type for frontend rendering.
     *
     * Examples:
     * TEXT
     * DRAFT_EVENTS
     * PUBLISHED_EVENTS
     * REVENUE_SUMMARY
     * SUBSCRIPTION_INFO
     */
    private String type;

    /**
     * Structured data returned by the tool.
     */
    private Object data;
}