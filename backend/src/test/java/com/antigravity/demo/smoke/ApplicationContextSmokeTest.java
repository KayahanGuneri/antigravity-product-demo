package com.antigravity.demo.smoke;

import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextSmokeTest extends PostgresTestContainerConfig {

    @Test
    void contextLoads() {
        // Just verifying the context loads with Testcontainers and Flyway
    }
}
