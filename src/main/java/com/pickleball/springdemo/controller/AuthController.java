package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.model.User;
import com.pickleball.springdemo.service.UserService;
import com.pickleball.springdemo.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // DTO class for response
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);

            // Optionally, create JWT right away
            String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());

            return ResponseEntity.ok().body(
                    new RegistrationResponse("User registered successfully", token)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new RegistrationResponse(e.getMessage(), null)
            );
        }
    }

    // DTO class for response
    public static class RegistrationResponse {
        private String message;
        private String token;

        public RegistrationResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() { return message; }
        public String getToken() { return token; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username,
                                       @RequestParam String password,
                                       HttpServletResponse response,
                                       HttpSession session,
                                       Model model) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT
            String jwtToken = jwtTokenProvider.generateToken(authentication);

            // Log all data
            System.out.println("=== LOGIN INFO ===");
            System.out.println("Username: " + username);
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("JWT Token: " + jwtToken);
            System.out.println("==================");

            // Store JWT in HTTP-only cookie
            Cookie jwtCookie = new Cookie("JWT", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // true in production
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);

            response.addCookie(jwtCookie);

            // Return token in response body too
            Map<String, Object> body = new HashMap<>();
            body.put("token", jwtToken);
            body.put("username", username);
            body.put("roles", authentication.getAuthorities());

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
