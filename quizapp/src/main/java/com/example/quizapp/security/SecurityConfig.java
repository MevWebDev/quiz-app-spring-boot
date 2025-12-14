package com.example.quizapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 * Implements SecurityFilterChain, authorizeHttpRequests, requestMatchers, formLogin.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder bean for password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Authorize HTTP Requests with requestMatchers
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // Swagger/OpenAPI
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                
                // API endpoints - public for now
                .requestMatchers("/api/v1/**").permitAll()
                
                // Actuator
                .requestMatchers("/actuator/**").permitAll()
                
                // Auth pages
                .requestMatchers("/login", "/register").permitAll()
                
                // Home
                .requestMatchers("/", "/home").permitAll()
                
                // Game pages - public (play quiz without login)
                .requestMatchers("/play/**", "/submit/**", "/result/**", "/ranking/**", "/ranking").permitAll()
                
                // Quiz list and view - public (GET only)
                .requestMatchers(HttpMethod.GET, "/quizzes").permitAll()
                .requestMatchers(HttpMethod.GET, "/quizzes/{id}").permitAll()
                
                // Quiz management - Admin only
                .requestMatchers(HttpMethod.GET, "/quizzes/new").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/quizzes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/quizzes/*/edit").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/quizzes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/quizzes/*/delete").hasRole("ADMIN")
                
                // Admin area
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Form Login configuration
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // CSRF configuration - disable for API endpoints
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            );

        return http.build();
    }
}
