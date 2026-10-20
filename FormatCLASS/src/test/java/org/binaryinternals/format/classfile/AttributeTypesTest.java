package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.classfile.Attributes;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.binaryinternals.format.classfile.attribute.attribute_info.AttributeTypes;
import org.junit.jupiter.api.Test;

class AttributeTypesTest {

    /// javac specific debugging attributes, which are not in the JVM Spec.
    private static final Set<String> NOT_SUPPORTED = Set.of(
            Attributes.NAME_CHARACTER_RANGE_TABLE, Attributes.NAME_COMPILATION_ID, Attributes.NAME_SOURCE_ID);

    @Test
    void everyJdkAttributeIsKnown() throws IllegalAccessException {
        final Set<String> missing = new TreeSet<>();
        for (Field f : Attributes.class.getFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getName().startsWith("NAME_")) {
                final String name = (String) f.get(null);
                if (Arrays.stream(AttributeTypes.values()).noneMatch(t -> t.getName().equals(name))) {
                    missing.add(name);
                }
            }
        }
        assertEquals(new TreeSet<>(NOT_SUPPORTED), missing);
    }
}
