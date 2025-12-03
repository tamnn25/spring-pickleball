package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.dto.UserRegistrationDto;
import com.pickleball.springdemo.model.Role;
import com.pickleball.springdemo.model.User;
import com.pickleball.springdemo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // LIST USERS
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users/list";  // templates/users/list.html
    }

    // SHOW CREATE FORM
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create";  // templates/users/create.html
    }

    // POST CREATE USER
    @PostMapping("/create")
    public String createUser(
            @Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult bindingResult,
            Model model) {

        System.out.println("Has errors? " + bindingResult.hasErrors());

        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "users/create";  // back to form
        }

        // Map DTO to entity
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhone_number(userDto.getPhone_number());
        user.setRoles(Set.of(new Role("ROLE_USER"))); // default role

        userRepository.save(user);
        return "redirect:/users";
    }

    // SHOW EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Invalid user ID:" + id));

        model.addAttribute("user", user);
        return "users/edit";  // templates/users/edit.html
    }

    // POST UPDATE USER
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID:" + id));

        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPhone_number(user.getPhone_number());
        // existing.setPassword(user.getPassword()); // optional

        userRepository.save(existing);
        return "redirect:/users";
    }

    // DELETE USER
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
