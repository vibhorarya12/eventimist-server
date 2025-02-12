package com.eventimist.server.service;

import com.eventimist.server.dto.userDTO.*;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserAuthService {
    UserRegisterResponseDTO registerUser(UserRegisterDTO userRegisterDTO);
    UserLoginResponseDTO loginUser (UserLoginDTO userLoginDTO);
    UserLoginResponseDTO loginWithOauth (UserOauthLoginDTO userOauthLoginDTO);
    UserRegisterResponseDTO registerWithOauth (UserOauthRegisterDTO userOauthRegisterDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);


}
