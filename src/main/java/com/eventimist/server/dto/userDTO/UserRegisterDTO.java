package com.eventimist.server.dto.userDTO;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String name;
    private String email;
    private String password;

}
