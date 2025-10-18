package com.example.authservice.api;

import com.example.authservice.api.dto.*;
import com.example.authservice.security.JwtService;
import com.example.authservice.user.Role;
import com.example.authservice.user.User;
import com.example.authservice.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        if (users.existsByEmail(req.email().toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email().toLowerCase());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRoles(Set.of(Role.USER));
        users.save(u);
        return ResponseEntity.ok(Map.of("message", "Registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String token = jwt.generateToken(principal,
                Map.of("roles", principal.getAuthorities().stream().map(Object::toString).toList()));

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        var user = users.findByEmail(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(new MeResponse(
                user.getId(), user.getName(), user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet())
        ));
    }
}
