package br.com.bruno.barbosa.student_helper_backend.config.security;

import br.com.bruno.barbosa.student_helper_backend.domain.enumeration.RoleEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(request -> {
                    var cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("http://localhost:8081"));
                    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    cors.setAllowedHeaders(List.of("*"));
                    return cors;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/teachers/register").permitAll()
                                .requestMatchers("/schools/register").permitAll()
                                .requestMatchers("/students/register").permitAll()
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/users").permitAll()
                                .requestMatchers("/school/**").hasRole(RoleEnum.SCHOOL.name())
                                .requestMatchers("/teacher/**").hasRole(RoleEnum.TEACHER.name())
                                .requestMatchers("/student/**").hasRole(RoleEnum.STUDENT.name())
                                .requestMatchers("/teacher/**").authenticated()
                                .requestMatchers("/appointments/**").authenticated()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(userDetailsService, jwtUtil);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}