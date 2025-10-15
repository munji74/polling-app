package com.pollingapp.authservice.auth;

import com.pollingapp.authservice.auth.dto.AuthResponse;
import com.pollingapp.authservice.auth.dto.LoginRequest;
import com.pollingapp.authservice.auth.dto.RegisterRequest;
import com.pollingapp.authservice.security.JwtService;
import com.pollingapp.authservice.user.User;
import com.pollingapp.authservice.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User u = new User(req.username(), encoder.encode(req.password()), "USER");
        users.save(u);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        var user = users.findByUsername(req.username()).orElse(null);
        if (user == null || !encoder.matches(req.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwt.generate(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token, jwt.getExpirySeconds()));
    }
}
