package com.eventimist.server.controllers;


import com.eventimist.server.dto.LoginDTO;
import com.eventimist.server.dto.LoginResponseDTO;
import com.eventimist.server.dto.RegisterDTO;
import com.eventimist.server.dto.RegisterResponseDTO;
import com.eventimist.server.service.AuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("register")
    public ResponseEntity<?>register(@RequestBody RegisterDTO registerDTO){
        try {
            authService.registerUser(registerDTO);
            RegisterResponseDTO response = new RegisterResponseDTO();
            String jwtToken = jwtUtil.generateToken(registerDTO.getEmail());
            response.setToken(jwtToken);
            response.setName(registerDTO.getName());
            response.setEmail(registerDTO.getEmail());
            return  new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e){

            return new ResponseEntity<>("Error creating user", HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping("login")
    public  ResponseEntity<?>login(@RequestBody LoginDTO loginDTO){
        LoginResponseDTO response  = authService.loginUser(loginDTO);
        boolean isAuthenticated = response.isAuthenticated();
        if (isAuthenticated) {
            String jwtToken = jwtUtil.generateToken(loginDTO.getEmail());
           response.setToken(jwtToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        // Remove any leading/trailing whitespace and convert to lowercase
        email = email.trim().toLowerCase();

        boolean emailExists = authService.checkEmailExists(email);

        if (emailExists) {
            return ResponseEntity.ok(Map.of("message", "Email exists", "exists", true));
        } else {
            return ResponseEntity.ok(Map.of("message", "Email not found", "exists", false));
        }
    }


}
