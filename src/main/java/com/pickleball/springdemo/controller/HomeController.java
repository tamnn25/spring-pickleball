package com.pickleball.springdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("pageTitle", "Home - Pickleball App");
        model.addAttribute("content", "Home");
        return "layout/main";
    }
}
