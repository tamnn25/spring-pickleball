package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.config.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // API Login endpoint that returns JSON with JWT token
//    @PostMapping("/api/auth/login")
//    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest) {
//        System.out.println("API login called for user: " + loginRequest.getUsername());
//
//        try {
//            // Authenticate user
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getUsername(),
//                            loginRequest.getPassword()
//                    )
//            );
//
//            // Set authentication in security context
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // Generate JWT token
//            String jwtToken = jwtTokenProvider.generateToken(authentication);
//
//            // Create response
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Login successful");
//            response.put("token", jwtToken);
//            response.put("username", authentication.getName());
//            response.put("token_type", "Bearer");
//            response.put("expires_in", 86400); // 24 hours in seconds
//
//            System.out.println("API login successful for user: " + loginRequest.getUsername());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            System.out.println("error trace" + e);
//            System.out.println("API login failed: " + e.getMessage());
//
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Invalid username or password");
//            errorResponse.put("error", e.getMessage());
//
//            return ResponseEntity.status(401).body(errorResponse);
//        }
//    }

    // API endpoint to validate token
    @PostMapping("/api/auth/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenValidationRequest validationRequest) {
        try {
            String token = validationRequest.getToken();
            boolean isValid = jwtTokenProvider.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);

            if (isValid) {
                String username = jwtTokenProvider.getUsername(token);
                response.put("username", username);
                response.put("message", "Token is valid");
            } else {
                response.put("message", "Token is invalid or expired");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "Token validation failed");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // API endpoint to get current user info
    @PostMapping("/api/auth/user")
    public ResponseEntity<?> getUserInfo(@RequestBody TokenRequest tokenRequest) {
        try {
            String token = tokenRequest.getToken();
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", username);
                userInfo.put("authenticated", true);

                return ResponseEntity.ok(userInfo);
            } else {
                return ResponseEntity.status(401).body(
                        Map.of("error", "Invalid token")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", "Token validation failed: " + e.getMessage())
            );
        }
    }

    // Request DTO classes
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenRequest {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class TokenValidationRequest {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public String findMultipleNumber(int number)
    {
        if (number % 3 == 0 && number % 5 == 0){
            return "FizzBuzz";
        }

        if (number % 3 == 0){
            return "Fizz";
        }

       if (number % 5 == 0) {
           return "Buzz";
       }

       return "Param invalid";
    }
}