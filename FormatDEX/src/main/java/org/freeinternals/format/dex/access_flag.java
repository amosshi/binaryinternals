/*
 * access_flag.java    June 21, 2021, 10:18
 *
 * Copyright 2021, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.freeinternals.format.classfile.AccessFlag;

/**
 * Bit-fields of these flags are used to indicate the accessibility and overall
 * properties of classes and class members.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from DEX Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the DEX spec name instead
 * java:S1104 - Class variable fields should not have public accessibility --- No, we like the simplified final value manner
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116", "java:S1104"})
public class access_flag {

    /**
     * Access flags for Classes (and InnerClass annotations).
     */
    public static final List<AccessFlag> ForClass = Collections.synchronizedList(new ArrayList<>());
    /**
     * Access flags for Fields.
     */
    public static final List<AccessFlag> ForField = Collections.synchronizedList(new ArrayList<>());
    /**
     * Access flags for Methods.
     */
    public static final List<AccessFlag> ForMethod = Collections.synchronizedList(new ArrayList<>());

    static {
        // For Classes (and InnerClass annotations)
        access_flag.ForClass.add(AccessFlag.ACC_PUBLIC);
        access_flag.ForClass.add(AccessFlag.ACC_PRIVATE);
        access_flag.ForClass.add(AccessFlag.ACC_PROTECTED);
        access_flag.ForClass.add(AccessFlag.ACC_STATIC);
        access_flag.ForClass.add(AccessFlag.ACC_FINAL);
        access_flag.ForClass.add(AccessFlag.ACC_INTERFACE);
        access_flag.ForClass.add(AccessFlag.ACC_ABSTRACT);
        access_flag.ForClass.add(AccessFlag.ACC_SYNTHETIC);
        access_flag.ForClass.add(AccessFlag.ACC_ANNOTATION);
        access_flag.ForClass.add(AccessFlag.ACC_ENUM);

        // For Fields
        access_flag.ForField.add(AccessFlag.ACC_PUBLIC);
        access_flag.ForField.add(AccessFlag.ACC_PRIVATE);
        access_flag.ForField.add(AccessFlag.ACC_PROTECTED);
        access_flag.ForField.add(AccessFlag.ACC_STATIC);
        access_flag.ForField.add(AccessFlag.ACC_FINAL);
        access_flag.ForField.add(AccessFlag.ACC_VOLATILE);
        access_flag.ForField.add(AccessFlag.ACC_TRANSIENT);
        access_flag.ForField.add(AccessFlag.ACC_SYNTHETIC);
        access_flag.ForField.add(AccessFlag.ACC_ENUM);

        // For Methods
        access_flag.ForMethod.add(AccessFlag.ACC_PUBLIC);
        access_flag.ForMethod.add(AccessFlag.ACC_PRIVATE);
        access_flag.ForMethod.add(AccessFlag.ACC_PROTECTED);
        access_flag.ForMethod.add(AccessFlag.ACC_STATIC);
        access_flag.ForMethod.add(AccessFlag.ACC_FINAL);
        access_flag.ForMethod.add(AccessFlag.ACC_SYNCHRONIZED);
        access_flag.ForMethod.add(AccessFlag.ACC_BRIDGE);
        access_flag.ForMethod.add(AccessFlag.ACC_VARARGS);
        access_flag.ForMethod.add(AccessFlag.ACC_ABSTRACT);
        access_flag.ForMethod.add(AccessFlag.ACC_STRICT);
        access_flag.ForMethod.add(AccessFlag.ACC_SYNTHETIC);
        access_flag.ForMethod.add(AccessFlag.ACC_CONSTRUCTOR);
        access_flag.ForMethod.add(AccessFlag.ACC_DECLARED_SYNCHRONIZED);
    }

    /**
     * Private constructor for utility class.
     */
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
