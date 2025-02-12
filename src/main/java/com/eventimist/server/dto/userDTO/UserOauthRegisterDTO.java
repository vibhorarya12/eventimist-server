package com.eventimist.server.dto.userDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserOauthRegisterDTO {
    @NotBlank(message = "name id is required")
    private String name;
    @NotBlank(message = "email id is required")
    private String email;
    @NotBlank(message = "Profile image is required")
    private String profilePic;

    @NotBlank(message = "clerk session id is required")
    private String clerkSessionId;
}
