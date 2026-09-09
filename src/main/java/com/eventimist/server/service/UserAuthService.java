package com.eventimist.server.service;

import com.eventimist.server.dto.userDTO.*;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserAuthService {
    UserAuthResponseDTO registerUser(UserRegisterDTO userRegisterDTO);
    UserAuthResponseDTO loginUser (UserLoginDTO userLoginDTO);
    UserAuthResponseDTO loginWithOauth (UserOauthLoginDTO userOauthLoginDTO);
    UserAuthResponseDTO registerWithOauth (UserOauthRegisterDTO userOauthRegisterDTO);
    UserDetails loadByEmail(String email);
    boolean checkEmailExists(String email);
    UserAuthResponseDTO refreshAccessToken(
            String refreshToken
    );
    void revokeRefreshToken(
            String refreshToken
    );

    
}
