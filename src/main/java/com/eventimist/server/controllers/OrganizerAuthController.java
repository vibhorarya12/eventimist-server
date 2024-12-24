package com.eventimist.server.controllers;


import com.eventimist.server.dto.organizerDTO.OrganizerLoginDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerLoginResponseDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterResponseDTO;
import com.eventimist.server.entities.OrganizerEntity;
import com.eventimist.server.repository.OrganizerRepository;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/organizer")
public class OrganizerAuthController {

    @Autowired
   private OrganizerAuthService organizerAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<?>login(@RequestBody OrganizerLoginDTO organizerLoginDTO) {

          OrganizerLoginResponseDTO organizerLoginResponseDTO = organizerAuthService.organizerLogin(organizerLoginDTO);
          return  new ResponseEntity<>(organizerLoginResponseDTO , HttpStatus.OK);


    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody OrganizerRegisterDTO organizerRegisterDTO){

         OrganizerRegisterResponseDTO organizerRegisterResponseDTO  = organizerAuthService.registerOrganizer(organizerRegisterDTO);

           return  new ResponseEntity<>(organizerRegisterResponseDTO, HttpStatus.OK);

    }
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {

        // Call service to check email existence
        boolean emailExists = organizerAuthService.checkEmailExists(email);

        if (emailExists) {
            return ResponseEntity.ok(Map.of("message", "Email exists", "exists", true));
        } else {
            return ResponseEntity.ok(Map.of("message", "Email not found", "exists", false));
        }
    }
}



