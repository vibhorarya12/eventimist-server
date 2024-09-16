package com.eventimist.server.service;

import com.eventimist.server.dto.UserDTO;
import com.eventimist.server.entities.UserEntity;

public interface UserService {
    void registerUser(UserDTO userDTO);

}
