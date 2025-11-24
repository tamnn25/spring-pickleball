package com.pickleball.springdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController   // Use @RestController for JSON responses
public class HelloController {

    @GetMapping("/home")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Pickleball!");
        response.put("status", "success");
        return response;
    }
}
