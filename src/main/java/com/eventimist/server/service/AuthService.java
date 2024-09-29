package com.eventimist.server.service;

import com.eventimist.server.dto.LoginDTO;
import com.eventimist.server.dto.LoginResponseDTO;
import com.eventimist.server.dto.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    void registerUser(RegisterDTO registerDTO);
    LoginResponseDTO loginUser (LoginDTO loginDTO);

    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);


}
