package org.binaryinternals.format.jpeg.tiff;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks for the message key constants of a resource bundle holder, like [IFDMessage].
final class MessageKeys {

    private MessageKeys() {
    }

    /// Whether `key` exists, by looking it up with `getString`.
    ///
    /// @param getString The lookup, like [IFDMessage#getString(String)]
    /// @param key The key
    /// @return `true` if the bundle has the key
    static boolean exists(final UnaryOperator<String> getString, final String key) {
        try {
            getString.apply(key);
            return true;
        } catch (MissingResourceException _) {
            return false;
        }
    }

    /// Assert every `KEY_` constant of `holder` exists in its bundle. Constants starting with
    /// `KEY_FORMAT_` are key formats, not keys, and are skipped.
    ///
    /// @param holder The class with the `KEY_` constants
    /// @param getString The lookup of the bundle
    /// @throws IllegalAccessException A constant is not readable
    static void assertConstantsExist(final Class<?> holder, final UnaryOperator<String> getString)
            throws IllegalAccessException {
        final List<String> missing = new ArrayList<>();
        int checked = 0;
        for (final Field field : holder.getFields()) {
            final boolean key = Modifier.isStatic(field.getModifiers())
                    && field.getType() == String.class
                    && field.getName().startsWith("KEY_")
                    && !field.getName().startsWith("KEY_FORMAT_");
            if (key) {
                final String value = (String) field.get(null);
                if (!exists(getString, value)) {
                    missing.add(field.getName() + " -> " + value);
                }
                checked++;
            }
        }

        assertTrue(checked > 0, "No KEY_ constant was found in " + holder.getSimpleName());
        assertEquals(List.of(), missing, "Missing in " + holder.getSimpleName() + ".properties");
    }
}
