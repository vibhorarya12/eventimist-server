package com.eventimist.server.controllers;

import com.cloudinary.Cloudinary;
import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.dto.organizerActionsDTO.GetEventsResponseDTO;
import com.eventimist.server.dto.organizerActionsDTO.TestingDTO;
import com.eventimist.server.service.OrganizerActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/organizer")
public class OrganizerActionsControllers {

    @Autowired
    private OrganizerActionsService organizerActionsService;

    @Autowired
    private Cloudinary cloudinary;


    @PostMapping("check")
    public ResponseEntity<?> check(
            @ModelAttribute TestingDTO testingDTO,
            @RequestPart("file") MultipartFile file
    ) {
        return new ResponseEntity<>(testingDTO.getTitle() + file.getOriginalFilename(), HttpStatus.OK);
    }



    @PostMapping("create-event")
    public ResponseEntity<?> createEvent(
            @ModelAttribute CreateEventDTO createEventDTO,
            @RequestPart("files") MultipartFile[] files) {

        // Call the service layer to create the event
        organizerActionsService.createEvent(createEventDTO, files);

        // Return a success response
        return new ResponseEntity<>(createEventDTO, HttpStatus.CREATED);
    }


    @GetMapping("all-events")
    public ResponseEntity<?> getAllEvents(@RequestParam Long organizerId) {
        List<GetEventsResponseDTO> events = organizerActionsService.getEventsByOrganizerId(organizerId);
        return new ResponseEntity<>(events, HttpStatus.OK);
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
