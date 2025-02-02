package com.eventimist.server.controllers;

import com.cloudinary.Cloudinary;
import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.dto.organizerActionsDTO.GetEventsResponseDTO;
import com.eventimist.server.dto.organizerActionsDTO.TestingDTO;
import com.eventimist.server.dto.organizerActionsDTO.UpdateProfileDTO;
import com.eventimist.server.service.OrganizerActionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/organizer")
public class OrganizerActionsControllers {

    @Autowired
    private OrganizerActionsService organizerActionsService;

    @Autowired
    private Cloudinary cloudinary;


    @PostMapping("check")
    public ResponseEntity<?> check(@RequestPart("files") MultipartFile[] files) {
        try {
            // Collect all file names
            List<String> fileNames = Arrays.stream(files)
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(fileNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing files", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("create-event")
    public ResponseEntity<?> createEvent(@ModelAttribute CreateEventDTO createEventDTO,
                                         @RequestPart("files") MultipartFile[] files) {
        organizerActionsService.createEvent(createEventDTO, files);

        // Return a success response
        return new ResponseEntity<>(createEventDTO, HttpStatus.CREATED);
    }

    // get all events by organizer ID //
    @GetMapping("all-events")
    public ResponseEntity<?> getAllEvents() {
        List<GetEventsResponseDTO> events = organizerActionsService.getEventsByOrganizerId();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }


    @PatchMapping("update-profile-info")
    public ResponseEntity<?> updateProfileInfo(@RequestBody UpdateProfileDTO updateProfileDTO){

        UpdateProfileDTO responseDTO  = organizerActionsService.updateProfileInfo(updateProfileDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);

    }
    @PatchMapping("update-image")
    public ResponseEntity<?> updateImage(
            @RequestPart("image") MultipartFile file,
            @RequestPart("type") String type) {

        try {

            return new ResponseEntity<>(type, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return new ResponseEntity<>(uploadResult, HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception by returning an appropriate error response
            return new ResponseEntity<>("Failed to upload image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
