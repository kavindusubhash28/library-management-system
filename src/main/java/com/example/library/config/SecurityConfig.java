package com.example.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.library.util.JwtUtil;
import com.example.library.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Password encoder bean for hashing user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil, UserRepository userRepository) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            // Configure endpoint access rules
            .authorizeHttpRequests(auth -> auth
                // Publicly accessible endpoints and static pages
                .requestMatchers(
                    "/api/auth/**",
                    "/register.html",
                    "/login.html",
                    "/dashboard.html",
                    "/user_dashboard.html",
                    "/borrow_records.html",
                    "/api/books",
                    "/api/books/search"
                ).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Use stateless session management (no HTTP session)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Add custom JWT authentication filter before the default username/password filter
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Custom filter to authenticate requests using JWT tokens
    class JwtAuthenticationFilter extends BasicAuthenticationFilter {
        private final JwtUtil jwtUtil;
        private final UserRepository userRepository;

        public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
            super(authentication -> authentication);
            this.jwtUtil = jwtUtil;
            this.userRepository = userRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // Extract Authorization header
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                // Extract email (username) from JWT
                String email = jwtUtil.extractUsername(token);
                // Validate token and set authentication if valid
                if (email != null && jwtUtil.validateToken(token, email)) {
                    UserDetails userDetails = userRepository.findByEmail(email)
                            .map(user -> org.springframework.security.core.userdetails.User
                                    .withUsername(user.getEmail())
                                    .password(user.getPassword())
                                    // Prefix role with ROLE_ for Spring Security compatibility
                                    .authorities("ROLE_" + user.getRole())
                                    .build())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // Continue filter chain
            chain.doFilter(request, response);
        }
    }
} 