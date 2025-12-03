package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.dto.UserRegistrationDto;
import com.pickleball.springdemo.model.Role;
import com.pickleball.springdemo.model.User;
import com.pickleball.springdemo.repository.RoleRepository;
import com.pickleball.springdemo.repository.UserRepository;
import com.pickleball.springdemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import com.pickleball.springdemo.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
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

        System.out.println("=== Create User Called ===");

        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors found:");
            bindingResult.getAllErrors().forEach(error ->
                    System.out.println(error.getDefaultMessage())
            );
            return "users/create";
        }




        try {
            // Map DTO to entity
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setPhone_number(userDto.getPhone_number());
            user.setPassword(userDto.getPassword());

            // Fetch ROLE_USER
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.setRoles(Set.of(role));

            System.out.println("User before register: " + user);
            System.out.println("User DTO password: " + userDto.getPassword());
            System.out.println("User entity password: " + user.getPassword());

            // Register user
            User savedUser = userService.register(user);

            System.out.println("User saved: " + savedUser);

            return "redirect:/users";
        } catch (Exception e) {
            System.err.println("Exception while creating user:");
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "users/create"; // back to form with error
        }
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
