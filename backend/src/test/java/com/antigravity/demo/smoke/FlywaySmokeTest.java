package com.antigravity.demo.smoke;

import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FlywaySmokeTest extends PostgresTestContainerConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void migrationsAreApplied() {
        // Verify that the users table exists (created by V2)
        Integer usersTableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_name = 'users'",
                Integer.class);
        Assertions.assertEquals(1, usersTableCount, "Users table should exist");

        // Verify that the products table exists (created by V1)
        Integer productsTableCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.tables WHERE table_name = 'products'",
                Integer.class);
        Assertions.assertEquals(1, productsTableCount, "Products table should exist");
    }
}
