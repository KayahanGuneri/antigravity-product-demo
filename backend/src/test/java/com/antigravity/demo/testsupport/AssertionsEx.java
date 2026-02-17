package com.antigravity.demo.testsupport;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Custom assertion helpers for unit tests.
 */
public class AssertionsEx {

    public static void assertUuid(UUID actual) {
        assertNotNull(actual, "UUID should not be null");
    }

    public static void assertNearNow(Instant actual) {
        assertNearNow(actual, Duration.ofSeconds(5));
    }

    public static void assertNearNow(Instant actual, Duration tolerance) {
        assertNotNull(actual, "Instant should not be null");
        Instant now = Instant.now();
        Duration diff = Duration.between(actual, now).abs();
        assertTrue(diff.compareTo(tolerance) <= 0,
                String.format("Instant %s is not within %s of %s (diff: %s)", actual, tolerance, now, diff));
    }
}
