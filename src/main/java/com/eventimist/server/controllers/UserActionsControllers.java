package com.eventimist.server.controllers;
import com.eventimist.server.dto.UserActionsDTO.EventInteractionsResponseDTO;
import com.eventimist.server.dto.UserActionsDTO.EventsResponseDTO;
import com.eventimist.server.dto.UserActionsDTO.NearbyEventsDTO;
import com.eventimist.server.dto.common.ApiResponseDTO;
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


    //rsvp events //
    @PostMapping("event/rsvp/{eventId}")
    public ResponseEntity<?>rsvpEvent(@PathVariable Long eventId ){

        userActionsService.rsvpEvent(eventId);

        return ResponseEntity.ok(ApiResponseDTO.success("event rsvp  done"));
    }


    // get rsvp events //

    @GetMapping("events/rsvp")
    public  ResponseEntity<?>getRsvpEvents(){

        List<EventsResponseDTO> events = userActionsService.getRsvpEvents();

        return  ResponseEntity.ok(events);

    }

  // delete RSVP events //

    @DeleteMapping("events/rsvp/{eventId}")
    public  ResponseEntity<?> removeRsvpEvents(@PathVariable Long eventId){

         userActionsService.removeRsvp(eventId);

      return  ResponseEntity.ok(ApiResponseDTO.success("event removed from rsvp"));
    }


// event interactions for returning rsvped event id's //
    // UserActionsController.java

    @GetMapping("/event/interactions")
    public ResponseEntity<?> getEventInteractions() {

        EventInteractionsResponseDTO response =
                userActionsService.getEventInteractions();

        return ResponseEntity.ok(response);
    }



    

    @PostMapping ("bookmark-event/{eventId}")
    ResponseEntity<?> bookMarkEvent (@PathVariable Long eventId){
        userActionsService.bookmarkEvents(eventId);
        return  ResponseEntity.ok(ApiResponseDTO.success("event bookmarked !!"));
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
//    @PostMapping("get-nearby-events")
//    ResponseEntity<?> getNearbyEvents(@RequestBody NearbyEventsDTO dto){
//
//
//        List<EventsResponseDTO> events =  userActionsService.getNearbyEvents(dto.getLatitude(), dto.getLongitude(),dto.getRadiusKm());
//
//        return  new ResponseEntity<>(events, HttpStatus.OK);
//
//    }

}
