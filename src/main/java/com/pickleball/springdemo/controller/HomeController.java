package com.pickleball.springdemo.controller;

import com.pickleball.springdemo.config.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private final JwtTokenProvider jwtTokenProvider;

    public HomeController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Accessing home page");

//        if (authentication != null && authentication.isAuthenticated()) {
//            System.out.println("Authentication: " + authentication);
//            System.out.println("Credentials: " + authentication.getCredentials());
//
//            // Get JWT token from credentials
//            Object credentials = authentication.getCredentials();
//            if (credentials instanceof String jwtToken) {
//                System.out.println("JWT Token: " + jwtToken);
//                model.addAttribute("jwtToken", jwtToken);
//            }
//        }
//        model.addAttribute("title", "Home - Pickleball");
        return "public/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "public/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "public/contact";
    }

//    @GetMapping("/dashboard")
//    public String dashboard(HttpServletRequest request, Model model) {
//        String token = null;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("JWT".equals(cookie.getName())) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            String username = jwtTokenProvider.getUsernameFromJWT(token);
//            model.addAttribute("username", username);
//            return "dashboard";
//        }
//
//        return "redirect:/login?error=unauthorized";
//    }

    @GetMapping("/dashboard")
    public String dashboard() {
        System.out.println("dashboard");
        return "dashboard/index";  // loads templates/dashboard/index.html
    }


    private String getTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}