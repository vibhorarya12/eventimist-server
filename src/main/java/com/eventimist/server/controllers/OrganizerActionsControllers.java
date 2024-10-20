package com.eventimist.server.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/organizer")
public class OrganizerEventControllers {

    @GetMapping("allEvents")
    public String allEvents(){

        return "all events";
    }


}
