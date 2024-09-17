package com.eventimist.server.service.implementService;

import com.eventimist.server.dto.LoginDTO;
import com.eventimist.server.dto.RegisterDTO;
import com.eventimist.server.entities.UserEntity;
import com.eventimist.server.repository.UserRepository;
import com.eventimist.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthServiceImplement implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(RegisterDTO registerDTO){

        UserEntity user = mapDtoToEntity(registerDTO);
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);

         userRepository.save(user);
    }


    @Override
    public  boolean loginUser(LoginDTO loginDTO){
        Optional<UserEntity>  userOptional = userRepository.findByEmail(loginDTO.getEmail());
        if(userOptional.isPresent()){
            UserEntity userEntity = userOptional.get();

            return  passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword());
        }
        return  false;
    }

    private  UserEntity mapDtoToEntity(RegisterDTO registerDTO){
        UserEntity user = new UserEntity();
         user.setName( registerDTO.getName());
         user.setEmail(registerDTO.getEmail());

        return  user;

    }
    @Override
    public UserDetails loadByEmail(String email) throws UsernameNotFoundException {
        // Load user by email from the database
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        UserEntity user = userOptional.get();
        // Return UserDetails (Spring Security requires this for authentication)
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

}
