package com.eventimist.server.service;

import com.eventimist.server.dto.LoginDTO;
import com.eventimist.server.dto.RegisterDTO;

public interface AuthService {
    void registerUser(RegisterDTO registerDTO);
    boolean loginUser (LoginDTO loginDTO);


}
