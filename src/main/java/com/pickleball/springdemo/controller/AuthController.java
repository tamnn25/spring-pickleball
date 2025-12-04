package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.dto.UserRegistrationDto;
import com.pickleball.springdemo.model.Role;
import com.pickleball.springdemo.model.User;
import com.pickleball.springdemo.repository.RoleRepository;
import com.pickleball.springdemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
public class AuthController {

    private final UserService service;
    private final RoleRepository roleRepository;

    public AuthController(UserService service, RoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login - Pickleball App");
        return "login"; // just the template name
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult bindingResult, User user) {

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("error message" + error.getDefaultMessage());
            });
        }

        // In your service or controller
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRoles(Set.of(userRole));


        service.register(user);
        return "redirect:/login?registered";
    }

//    @GetMapping("/users")
//    public String getAllUsers(Model model) {
//        model.addAttribute("users", service.getAll());
//        return "users/list";
//    }
}
