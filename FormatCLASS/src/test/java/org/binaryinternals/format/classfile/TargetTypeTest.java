package org.binaryinternals.format.classfile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.binaryinternals.format.classfile.attribute.RuntimeTypeAnnotations.type_annotation.TargetType;
import org.junit.jupiter.api.Test;

class TargetTypeTest {

    /// JVM Spec Table 4.7.20-A and 4.7.20-B: target_type values and their target_info item.
    private static final Map<Integer, String> UNIONS = Map.ofEntries(
            Map.entry(0x00, "type_parameter_target"), Map.entry(0x01, "type_parameter_target"),
            Map.entry(0x10, "supertype_target"),
            Map.entry(0x11, "type_parameter_bound_target"), Map.entry(0x12, "type_parameter_bound_target"),
            Map.entry(0x13, "empty_target"), Map.entry(0x14, "empty_target"), Map.entry(0x15, "empty_target"),
            Map.entry(0x16, "formal_parameter_target"),
            Map.entry(0x17, "throws_target"),
            Map.entry(0x40, "localvar_target"), Map.entry(0x41, "localvar_target"),
            Map.entry(0x42, "catch_target"),
            Map.entry(0x43, "offset_target"), Map.entry(0x44, "offset_target"), Map.entry(0x45, "offset_target"), Map.entry(0x46, "offset_target"),
            Map.entry(0x47, "type_argument_target"), Map.entry(0x48, "type_argument_target"), Map.entry(0x49, "type_argument_target"),
            Map.entry(0x4A, "type_argument_target"), Map.entry(0x4B, "type_argument_target"));

    @Test
    void unionNames() {
        for (int v = 0; v <= 0xFF; v++) {
            assertEquals(UNIONS.getOrDefault(v, "Un-recognized"), TargetType.getTargetUnionName((short) v), "0x%02X".formatted(v));
        }
    }
}
