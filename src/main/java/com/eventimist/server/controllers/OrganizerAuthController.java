package com.eventimist.server.controllers;


import com.eventimist.server.dto.organizerDTO.OrganizerLoginDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerLoginResponseDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterDTO;
import com.eventimist.server.dto.organizerDTO.OrganizerRegisterResponseDTO;
import com.eventimist.server.service.OrganizerAuthService;
import com.eventimist.server.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> register(@ModelAttribute OrganizerRegisterDTO organizerRegisterDTO , @RequestPart MultipartFile file){

         OrganizerRegisterResponseDTO organizerRegisterResponseDTO  = organizerAuthService.registerOrganizer(organizerRegisterDTO,file);

           return  new ResponseEntity<>(organizerRegisterResponseDTO, HttpStatus.OK);


    }

}
