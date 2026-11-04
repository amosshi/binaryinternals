/*
 * access_flag.java    June 21, 2021, 10:18
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.util.List;
import org.binaryinternals.format.classfile.AccessFlag;

/// Bit-fields of these flags are used to indicate the accessibility and overall
/// properties of classes and class members.
///
/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
/// java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
/// ```
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public final class access_flag {

    /// Access flags for Classes (and InnerClass annotations).
    public static final List<AccessFlag> ForClass = List.of(
            AccessFlag.ACC_PUBLIC,
            AccessFlag.ACC_PRIVATE,
            AccessFlag.ACC_PROTECTED,
            AccessFlag.ACC_STATIC,
            AccessFlag.ACC_FINAL,
            AccessFlag.ACC_INTERFACE,
            AccessFlag.ACC_ABSTRACT,
            AccessFlag.ACC_SYNTHETIC,
            AccessFlag.ACC_ANNOTATION,
            AccessFlag.ACC_ENUM);
    /// Access flags for Fields.
    public static final List<AccessFlag> ForField = List.of(
            AccessFlag.ACC_PUBLIC,
            AccessFlag.ACC_PRIVATE,
            AccessFlag.ACC_PROTECTED,
            AccessFlag.ACC_STATIC,
            AccessFlag.ACC_FINAL,
            AccessFlag.ACC_VOLATILE,
            AccessFlag.ACC_TRANSIENT,
            AccessFlag.ACC_SYNTHETIC,
            AccessFlag.ACC_ENUM);
    /// Access flags for Methods.
    public static final List<AccessFlag> ForMethod = List.of(
            AccessFlag.ACC_PUBLIC,
            AccessFlag.ACC_PRIVATE,
            AccessFlag.ACC_PROTECTED,
            AccessFlag.ACC_STATIC,
            AccessFlag.ACC_FINAL,
            AccessFlag.ACC_SYNCHRONIZED,
            AccessFlag.ACC_BRIDGE,
            AccessFlag.ACC_VARARGS,
            AccessFlag.ACC_ABSTRACT,
            AccessFlag.ACC_STRICT,
            AccessFlag.ACC_SYNTHETIC,
            AccessFlag.ACC_CONSTRUCTOR,
            AccessFlag.ACC_DECLARED_SYNCHRONIZED);

    /// Private constructor for utility class.
    private access_flag() {
    }

    public static String getClassModifier(long value) {
        return AccessFlag.getModifier(value, access_flag.ForClass);
    }

    public static String getFieldModifier(int value) {
        return AccessFlag.getModifier(value, access_flag.ForField);
    }

    public static String getMethodModifier(int value) {
        return AccessFlag.getModifier(value, access_flag.ForMethod);
    }
}
