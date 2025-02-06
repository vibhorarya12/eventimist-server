package com.eventimist.server.dto.organizerDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizerOauthRegisterDTO {
    @NotBlank(message = "Name is required.")
    private String name;
    @NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "No valid clerk session id provided")
    private String clerkSessionId;

    private  String profilePic;
}
