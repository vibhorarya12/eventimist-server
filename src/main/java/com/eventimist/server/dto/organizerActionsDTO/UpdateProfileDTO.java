package com.eventimist.server.dto.organizerActionsDTO;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String name;

    @Size(max = 75, message = "Bio must not exceed 75 characters")
    private String bio;
    private String location;

}
