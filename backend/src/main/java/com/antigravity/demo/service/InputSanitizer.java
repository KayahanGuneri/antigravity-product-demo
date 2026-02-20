package com.antigravity.demo.service;

import org.springframework.util.StringUtils;

/**
 * Utility for basic input sanitization to prevent XSS and control character
 * injection.
 */
public class InputSanitizer {

    private InputSanitizer() {
        // Utility class
    }

    /**
     * Sanitizes text input by trimming, checking length, and rejecting HTML-like
     * characters.
     * Throws IllegalArgumentException if validation fails.
     */
    public static String sanitize(String value, String fieldName, int maxLen, boolean required) {
        if (!StringUtils.hasText(value)) {
            if (required) {
                throw new IllegalArgumentException(fieldName + " is required and cannot be blank");
            }
            return null;
        }

        String trimmed = value.trim();

        if (trimmed.length() > maxLen) {
            throw new IllegalArgumentException(fieldName + " exceeds maximum length of " + maxLen);
        }

        // Reject HTML tags
        if (trimmed.contains("<") || trimmed.contains(">")) {
            throw new IllegalArgumentException(fieldName + " contains invalid characters (HTML tags not allowed)");
        }

        // Reject control characters (except common whitespace like \n, \r, \t)
        for (char c : trimmed.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                throw new IllegalArgumentException(fieldName + " contains restricted control characters");
            }
        }

        return trimmed;
    }
}
