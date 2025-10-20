package com.example.authservice.api;

import com.example.authservice.api.dto.AuthResponse;
import com.example.authservice.api.dto.LoginRequest;
import com.example.authservice.api.dto.MeResponse;
import com.example.authservice.api.dto.RegisterRequest;
import com.example.authservice.security.JwtService;
import com.example.authservice.user.Role;
import com.example.authservice.user.User;
import com.example.authservice.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        String email = req.email().toLowerCase();

        if (!req.password().equals(req.passwordConfirm())) {
            return ResponseEntity.badRequest().body(Map.of("passwordConfirm", "Passwords do not match"));
        }
        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("email", "Email already in use"));
        }

        User u = new User();
        u.setName(req.name());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRoles(Set.of(Role.USER));
        users.save(u);

        // (Optional) auto-login on register
        String token = jwt.generateToken(
                new org.springframework.security.core.userdetails.User(
                        email, "", java.util.List.of(() -> "ROLE_USER")
                ),
                Map.of("roles", java.util.List.of("ROLE_USER"))
        );

        return ResponseEntity
                .created(URI.create("/api/auth/users/" + u.getId()))
                .body(Map.of("accessToken", token, "tokenType", "Bearer"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            if (!(auth.getPrincipal() instanceof UserDetails principal)) {
                return ResponseEntity.status(500).body(Map.of("error", "Unexpected principal type"));
            }

            String token = jwt.generateToken(
                    principal,
                    Map.of("roles", principal.getAuthorities().stream().map(Object::toString).toList())
            );
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException badCreds) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception unexpected) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails principal)) {
            return ResponseEntity.status(401).build();
        }
        var user = users.findByEmail(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(new MeResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        ));
    }
}
