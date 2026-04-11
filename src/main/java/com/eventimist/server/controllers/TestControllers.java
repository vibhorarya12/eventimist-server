package com.eventimist.server.controllers;

import com.eventimist.server.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestControllers {

    @GetMapping("/test")
    public ResponseEntity<String> test (){
        throw new EntityNotFoundException("not found bhai");

    }
}
