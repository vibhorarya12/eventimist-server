package com.eventimist.server.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestControllers {

    @GetMapping("/test")
    public ResponseEntity<String> test (){
        try{
            return new ResponseEntity<>("success" , HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("aunauth", HttpStatus.UNAUTHORIZED);
        }

    }
}
