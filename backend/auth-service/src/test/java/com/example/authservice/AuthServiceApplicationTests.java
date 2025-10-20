package com.example.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "app.jwt.secret=test-secret-key-at-least-32-characters-long",
        "app.jwt.expires-in=3600000"
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}