/*
 * Json.java    October 20, 2026
 *
 * Copyright 2026, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.web;

/// Minimal JSON writing helpers, to keep the project free of 3rd party
/// dependencies.
///
/// @author Amos Shi
final class Json {

    /// Unicode LINE SEPARATOR (U+2028), valid in JSON but a line break in JavaScript.
    private static final char LINE_SEPARATOR = 0x2028;
    /// Unicode PARAGRAPH SEPARATOR (U+2029), valid in JSON but a line break in JavaScript.
    private static final char PARAGRAPH_SEPARATOR = 0x2029;

    private Json() {
    }

    /// Get `s` as a quoted JSON string.
    ///
    /// @param s The string
    /// @return JSON string
    static String string(final String s) {
        return string(new StringBuilder(), s).toString();
    }

    /// Append `s` as a quoted JSON string, or `null`.
    ///
    /// @param sb Target
    /// @param s The string
    /// @return `sb`
    static StringBuilder string(final StringBuilder sb, final String s) {
        if (s == null) {
            return sb.append("null");
        }
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                // < and > escaped too, so the JSON is safe to embed in HTML
                case '<', '>', LINE_SEPARATOR, PARAGRAPH_SEPARATOR -> sb.append("\\u%04x".formatted((int) c));
                default -> {
                    if (c < 0x20) {
                        sb.append("\\u%04x".formatted((int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"');
    }

    /// Get the JSON error body `{"status": 415, "error": "..."}`.
    ///
    /// @param status HTTP status
    /// @param message Error message
    /// @return JSON text
    static String error(final int status, final String message) {
        return "{\"status\":" + status + ",\"error\":" + string(message) + "}";
    }
}
