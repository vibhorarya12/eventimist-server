package com.eventimist.server.controllers;


import com.eventimist.server.dto.userDTO.*;
import com.eventimist.server.service.UserAuthService;
import com.eventimist.server.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
    public ResponseEntity<?>register(@RequestBody UserRegisterDTO userRegisterDTO){

           UserRegisterResponseDTO userRegisterResponseDTO = userAuthService.registerUser(userRegisterDTO);

            return  new ResponseEntity<>(userRegisterResponseDTO, HttpStatus.OK);

    }


    @PostMapping("oauthLogin")
    public ResponseEntity<?> oauthLogin (@Valid @RequestBody UserOauthLoginDTO userOauthLoginDTO){
                UserLoginResponseDTO userLoginResponseDTO = userAuthService.loginWithOauth(userOauthLoginDTO);
                return  new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }


    @PostMapping("login")
    public  ResponseEntity<?>login(@Valid @RequestBody UserLoginDTO userLoginDTO){

           UserLoginResponseDTO userLoginResponseDTO = userAuthService.loginUser(userLoginDTO);
            return  new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }

    @PostMapping("check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        // Remove any leading/trailing whitespace and convert to lowercase
        email = email.trim().toLowerCase();

        boolean emailExists = userAuthService.checkEmailExists(email);

        if (emailExists) {
            return ResponseEntity.ok(Map.of("message", "Email exists", "exists", true));
        } else {
            return ResponseEntity.ok(Map.of("message", "Email not found", "exists", false));
        }
    }


}
