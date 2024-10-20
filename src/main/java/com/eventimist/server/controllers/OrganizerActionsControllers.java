package com.eventimist.server.controllers;


import com.eventimist.server.dto.organizerActionsDTO.CreateEventDTO;
import com.eventimist.server.entities.EventEntity;
import com.eventimist.server.service.OrganizerActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/organizer")
public class OrganizerActionsControllers {

    @Autowired
    private OrganizerActionsService organizerActionsService;

    @PostMapping("create-event")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDTO createEventDTO){
          organizerActionsService.createEvent(createEventDTO);
          return  new ResponseEntity<>("event created successfully", HttpStatus.OK);
    }
    @GetMapping("all-events")
    public ResponseEntity<List<EventEntity>> getAllEvents(@RequestParam Long organizerId) {
        List<EventEntity> events = organizerActionsService.getEventsByOrganizerId(organizerId);
        return ResponseEntity.ok(events);

    }


}
