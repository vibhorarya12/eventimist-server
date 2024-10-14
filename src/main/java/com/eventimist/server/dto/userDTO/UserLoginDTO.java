package com.eventimist.server.dto.userDTO;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String email;
    private String password;
}