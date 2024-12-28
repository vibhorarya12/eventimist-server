package com.eventimist.server.dto.clerkResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClerkSessionResponseDTO {

    @JsonProperty("object")
    private String object;

    @JsonProperty("id")
    private String id;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("actor")
    private Object actor;

    @JsonProperty("last_active_at")
    private long lastActiveAt;

    @JsonProperty("expire_at")
    private long expireAt;

    @JsonProperty("abandon_at")
    private long abandonAt;

    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty("updated_at")
    private long updatedAt;
}
