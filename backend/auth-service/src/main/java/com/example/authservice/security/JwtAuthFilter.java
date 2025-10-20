package com.example.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ✅ Only skip OPTIONS requests and public endpoints
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        // ✅ Allow /api/auth/login and /api/auth/register to pass through
        // ✅ BUT process /api/auth/me (it will have no token, that's fine)
        return path.equals("/api/auth/login") || path.equals("/api/auth/register");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

        // ✅ Only process if Authorization header exists
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                System.out.println("🔐 JwtAuthFilter: Processing token for " + req.getRequestURI());

                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("✅ JwtAuthFilter: Token valid, username: " + username);

                    var userDetails = userDetailsService.loadUserByUsername(username);
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ JwtAuthFilter: Authentication set in SecurityContext");
                }
            } catch (Exception e) {
                System.out.println("❌ JwtAuthFilter: Token validation failed: " + e.getMessage());
                // Don't throw - let the request continue, Spring will return 401 if auth is required
            }
        } else {
            System.out.println("⏭️  JwtAuthFilter: No Authorization header for " + req.getRequestURI());
        }

        chain.doFilter(req, res);
    }
}