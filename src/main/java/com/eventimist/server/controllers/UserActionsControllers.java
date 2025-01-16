package com.eventimist.server.controllers;
import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;
import com.eventimist.server.dto.UserActionsDTO.NearbyEventsDTO;
import com.eventimist.server.service.UserActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserActionsControllers {
    @Autowired
    private UserActionsService userActionsService;


    @GetMapping
    ResponseEntity<?> check(){
        return  new ResponseEntity<>("success", HttpStatus.OK);

    }
    @PostMapping ("bookmark-event")
    ResponseEntity<?> bookMarkEvent (@RequestParam Long userId , @RequestParam Long eventId){
        userActionsService.bookmarkEvents(userId,eventId);
        return  new ResponseEntity<>("added", HttpStatus.OK);
    }
    @PostMapping ("attend-event")
    ResponseEntity<?> attendEvent(@RequestParam Long userId , @RequestParam Long eventId){
        userActionsService.attendEvents(userId, eventId);
        return  new ResponseEntity<>("updated", HttpStatus.OK );

    }
    @GetMapping("get-bookmarked-events")
    ResponseEntity<?> getBookMarkedEvents(@RequestParam Long userId){
        List<EventsResponseDTO> events = userActionsService.getBookmarkedEvents(userId);

        return  new ResponseEntity<>(events, HttpStatus.OK);
    }
    @GetMapping("get-attending-events")
    ResponseEntity<?> getAttendingEvents(@RequestParam Long userId){
        List<EventsResponseDTO> events = userActionsService.getAttendingEvents(userId);

        return  new ResponseEntity<>(events, HttpStatus.OK);
    }
    @PostMapping("get-nearby-events")
    ResponseEntity<?> getNearbyEvents(@RequestBody NearbyEventsDTO dto){


        List<EventsResponseDTO> events =  userActionsService.getNearbyEvents(dto.getLatitude(), dto.getLongitude(),dto.getRadiusKm());

        return  new ResponseEntity<>(events, HttpStatus.OK);
        
    }

}
