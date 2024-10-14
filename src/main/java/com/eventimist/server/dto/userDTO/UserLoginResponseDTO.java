package com.eventimist.server.dto.userDTO;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String name;
    private  String email;
    private  String token;
}
