package com.eventimist.server.controllers;


import com.eventimist.server.dto.publicDTO.DiscoverEventResponseDTO;
import com.eventimist.server.dto.publicDTO.DiscoverEventsRequestDTO;
import com.eventimist.server.dto.publicDTO.ViewEventResponseDTO;
import com.eventimist.server.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public")
public class PublicControllers {

    @Autowired
    private PublicService publicService;

    @GetMapping("/discover-events")
    public ResponseEntity<?> discoverEvents(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(required = false, defaultValue = "10") double radius
    ) {
        DiscoverEventsRequestDTO dto = new DiscoverEventsRequestDTO();
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setRadius(radius);

        return ResponseEntity.ok(
                publicService.discoverEvents(dto)
        );
    }
    @GetMapping("/event/{slug}")
    public ResponseEntity<?> getEvents(@PathVariable String slug){
        return ResponseEntity.ok(publicService.getEvent(slug)
        );

    }



}
