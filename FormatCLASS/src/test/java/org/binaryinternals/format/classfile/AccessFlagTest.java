package org.binaryinternals.format.classfile;

import static org.binaryinternals.format.classfile.AccessFlag.ACC_ABSTRACT;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_ANNOTATION;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_BRIDGE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_ENUM;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_FINAL;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_INTERFACE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_MANDATED;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_MODULE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_NATIVE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_PRIVATE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_PROTECTED;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_PUBLIC;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_STATIC;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_STATIC_PHASE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_STRICT;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_SUPER;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_SYNCHRONIZED;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_SYNTHETIC;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_TRANSITIVE;
import static org.binaryinternals.format.classfile.AccessFlag.ACC_VARARGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccessFlagTest {

    @Test
    void masksMatchJdk() {
        for (AccessFlag flag : AccessFlag.values()) {
            flag.toJdkFlag().ifPresent(jdk -> assertEquals(jdk.mask(), flag.intValue(), flag.name()));
        }
    }

    @Test
    void onlyDexFlagsHaveNoJdkFlag() {
        final EnumSet<AccessFlag> dexOnly = EnumSet.noneOf(AccessFlag.class);
        for (AccessFlag flag : AccessFlag.values()) {
            if (flag.toJdkFlag().isEmpty()) {
                dexOnly.add(flag);
            }
        }
        assertEquals(EnumSet.of(AccessFlag.ACC_CONSTRUCTOR, AccessFlag.ACC_DECLARED_SYNCHRONIZED), dexOnly);
    }

    @Test
    void listsPerLocation() {
        assertEquals(List.of(ACC_PUBLIC, ACC_FINAL, ACC_SUPER, ACC_INTERFACE, ACC_ABSTRACT, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM, ACC_MODULE),
                AccessFlag.ForClass);
        assertEquals(List.of(ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_BRIDGE, ACC_VARARGS,
                ACC_NATIVE, ACC_ABSTRACT, ACC_STRICT, ACC_SYNTHETIC), AccessFlag.ForMethod);
        assertEquals(List.of(ACC_TRANSITIVE, ACC_STATIC_PHASE, ACC_SYNTHETIC, ACC_MANDATED), AccessFlag.ForModuleRequires);
        assertEquals(List.of(ACC_FINAL, ACC_SYNTHETIC, ACC_MANDATED), AccessFlag.ForMethodParameters);
    }

    @Test
    void modifierText() {
        assertEquals("public static", AccessFlag.getMethodModifier(0x0009));
        assertEquals("public final super", AccessFlag.getClassModifier(0x0031));
        assertEquals("", AccessFlag.getFieldModifier(0));
        assertTrue(AccessFlag.getModifier(0x0020, AccessFlag.ForModuleRequires).contains("transitive"));
    }
}
