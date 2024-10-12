package com.eventimist.server.service;

import com.eventimist.server.dto.userDTO.UserLoginDTO;
import com.eventimist.server.dto.userDTO.UserLoginResponseDTO;
import com.eventimist.server.dto.userDTO.UserRegisterDTO;
import com.eventimist.server.dto.userDTO.UserRegisterResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserAuthService {
    UserRegisterResponseDTO registerUser(UserRegisterDTO userRegisterDTO);
    UserLoginResponseDTO loginUser (UserLoginDTO userLoginDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);


}
