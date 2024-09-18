package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.response.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            String role = user.getAuthorities().iterator().next().getAuthority();

            String token = Jwts.builder()
                    .setSubject(loginRequest.getUsername())
                    .claim("role", role)
                    .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                    .compact();

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            String redirectUrl;
            switch (role) {
                case "ROLE_TEACHER":
                    redirectUrl = "http://localhost:8081/teacher";
                    break;
                case "ROLE_STUDENT":
                    redirectUrl = "http://localhost:8081/student";
                    break;
                case "ROLE_SCHOOL":
                    redirectUrl = "http://localhost:8081/school";
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unknown role");
            }

            return ResponseEntity.ok(new LoginResponse(token, redirectUrl));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;

    }
}