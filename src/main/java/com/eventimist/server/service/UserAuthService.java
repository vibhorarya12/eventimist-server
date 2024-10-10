package com.eventimist.server.service;

import com.eventimist.server.dto.userDTO.LoginDTO;
import com.eventimist.server.dto.userDTO.LoginResponseDTO;
import com.eventimist.server.dto.userDTO.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    void registerUser(RegisterDTO registerDTO);
    LoginResponseDTO loginUser (LoginDTO loginDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);


}
