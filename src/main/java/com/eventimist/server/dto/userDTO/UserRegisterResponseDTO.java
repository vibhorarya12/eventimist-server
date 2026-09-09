package com.eventimist.server.dto.userDTO;


import lombok.Data;

@Data
public class UserRegisterResponseDTO {
    private String name ;
    private  String email ;
    private  String token ;
    private String refreshToken;
    private String profilePic;


}
