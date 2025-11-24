//package com.pickleball.springdemo.config;
//
//import com.pickleball.springdemo.service.UserDetailsServiceImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfigDemo {
//
//    private final UserDetailsServiceImpl userDetailsService;
//    private final JwtTokenProvider jwtTokenProvider;
//
//
//    public SecurityConfigDemo(UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
//        this.userDetailsService = userDetailsService;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        System.out.println("step 1");
//        http
//                // Disable CSRF for H2 console and APIs
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
//                )
//                // Allow H2 console to be displayed in a frame
//                .headers(headers -> headers
//                        .frameOptions(frame -> frame.sameOrigin())
//                )
//                // Authorization rules
//                .authorizeHttpRequests(auth -> auth
//                        // Static resources
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
//                        // Public pages
//                        .requestMatchers("/", "/home", "/public/**", "/register", "/login", "/login-process").permitAll()
//                        // Public API endpoints
//                        .requestMatchers("/api/auth/**").permitAll()
//                        // H2 console
//                        .requestMatchers("/h2-console/**").permitAll()
//                        // Protected pages
//                        .requestMatchers("/api/users/**").authenticated()
//                        .requestMatchers("/dashboard").authenticated()
//                        // Any other request requires authentication
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                )
//                .logout(logout -> logout.permitAll());
//
//
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
//    }
//}