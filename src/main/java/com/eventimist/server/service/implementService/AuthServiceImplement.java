package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.UserDTO;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplement  implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(UserDTO userDTO){

        UserEntity user = mapDtoToEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

         userRepository.save(user);
    }

    private  UserEntity mapDtoToEntity(UserDTO userDTO){
        UserEntity user = new UserEntity();
         user.setName( userDTO.getName());
         user.setEmail(userDTO.getEmail());

        return  user;

    }
}
