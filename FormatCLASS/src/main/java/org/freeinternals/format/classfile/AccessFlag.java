/*
 * AccessFlag.java    11:11, June 20, 2015
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.format.classfile.attribute.InnerClasses_attribute;
import org.freeinternals.format.classfile.attribute.MethodParameters_attribute;
import org.freeinternals.format.classfile.attribute.Module_attribute;

/**
 * Access flags are mask of flags used to denote access permissions to and
 * properties of this class file.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S2386 - Mutable fields should not be "public static" --- We keep the simplied public variable
 * </pre>
 */
@SuppressWarnings("java:S2386")
public enum AccessFlag {

    /**
     * Declared <code>public</code>; may be accessed from outside its package.
     */
    ACC_PUBLIC(0x0001, JavaLangSpec.Keyword.PUBLIC.text),
    /**
     * Declared <code>private</code>; usable only within the defining class.
     */
    ACC_PRIVATE(0x0002, JavaLangSpec.Keyword.PRIVATE.text),
    /**
     * Declared <code>protected</code>; may be accessed within subclasses.
     */
    ACC_PROTECTED(0x0004, JavaLangSpec.Keyword.PROTECTED.text),
    /**
     * Declared <code>static</code>.
     */
    ACC_STATIC(0x0008, JavaLangSpec.Keyword.STATIC.text),
    /**
     * Declared <code>final</code>; no subclasses allowed.
     */
    ACC_FINAL(0x0010, JavaLangSpec.Keyword.FINAL.text),
    /**
     * Indicates that this module ({@link Module_attribute}) is open.
     */
    ACC_OPEN(0x0010, JavaLangSpec.RestrictedKeyword.OPEN.keyword),
    /**
     * Treat superclass methods specially when invoked by the
     * <code>invokespecial</code> instruction.
     */
    ACC_SUPER(0x0020, JavaLangSpec.Keyword.SUPER.text),
    /**
     * Declared <code>synchronized</code>; invocation is wrapped by a monitor
     * use.
     */
    ACC_SYNCHRONIZED(0x0020, JavaLangSpec.Keyword.SYNCHRONIZED.text),
    /**
     * Indicates that any module which depends on the current module, implicitly
     * declares a dependence on the module indicated by this entry.
     */
    ACC_TRANSITIVE(0x0020, JavaLangSpec.RestrictedKeyword.TRANSITIVE.keyword),
    /**
     * Declared <code>volatile</code>; cannot be cached.
     */
    ACC_VOLATILE(0x0040, JavaLangSpec.Keyword.VOLATILE.text),
    /**
     * A bridge method, generated by the compiler.
     */
    ACC_BRIDGE(0x0040, "ACC_BRIDGE"),
    /**
     * Indicates that this dependence is mandatory in the static phase, i.e., at
     * compile time, but is optional in the dynamic phase, i.e., at run time.
     */
    ACC_STATIC_PHASE(0x0040, "ACC_STATIC_PHASE"),
    /**
     * Declared <code>transient</code>; not written or read by a persistent
     * object manager.
     */
    ACC_TRANSIENT(0x0080, JavaLangSpec.Keyword.TRANSIENT.text),
    /**
     * Declared with variable number of arguments.
     */
    ACC_VARARGS(0x0080, "ACC_VARARGS"),
    /**
     * Declared <code>native</code>; implemented in a language other than Java.
     */
    ACC_NATIVE(0x0100, JavaLangSpec.Keyword.NATIVE.text),
    /**
     * Is an interface, not a class.
     */
    ACC_INTERFACE(0x0200, JavaLangSpec.Keyword.INTERFACE.text),
    /**
     * Declared <code>abstract</code>; must not be instantiated.
     */
    ACC_ABSTRACT(0x0400, JavaLangSpec.Keyword.ABSTRACT.text),
    /**
     * Declared <code>strictfp</code>; floating-point mode is FP-strict.
     */
    ACC_STRICT(0x0800, JavaLangSpec.Keyword.STRICTFP.text),
    /**
     * Declared <code>synthetic</code>; not present in the source code.
     *
     * Indicates that this module ({@link Module_attribute}) was not explicitly
     * or implicitly declared.
     *
     * Indicates that this dependence was not explicitly or implicitly declared
     * in the source of the module declaration.
     *
     * Indicates that this export was not explicitly or implicitly declared in
     * the source of the module declaration.
     *
     * Indicates that this opening was not explicitly or implicitly declared in
     * the source of the module declaration.
     */
    ACC_SYNTHETIC(0x1000, "ACC_SYNTHETIC"),
    /**
     * Declared as an annotation type.
     */
    ACC_ANNOTATION(0x2000, "@interface"),
    /**
     * Declared as an <code>enum</code> type.
     */
    ACC_ENUM(0x4000, JavaLangSpec.Keyword.ENUM.text),
    /**
     * Is a module, not a class or interface.
     */
    ACC_MODULE(0x8000, JavaLangSpec.RestrictedKeyword.MODULE.keyword),
    /**
     * Indicates that the formal parameter was implicitly declared in source
     * code, according to the specification of the language in which the source
     * code was written (JLS §13.1). (The formal parameter is mandated by a
     * language specification, so all compilers for the language must emit it.)
     *
     * Indicates that this module was implicitly declared.
     *
     * Indicates that this dependence was implicitly declared in the source of
     * the module declaration.
     *
     * Indicates that this export was implicitly declared in the source of the
     * module declaration.
     *
     * Indicates that this opening was implicitly declared in the source of the
     * module declaration.
     */
    ACC_MANDATED(    0x8000, "ACC_MANDATED"),
    /**
     * Constructor method (class or instance initializer).
     * Used by Android dex.
     */
    ACC_CONSTRUCTOR(0x10000, "ACC_CONSTRUCTOR"),
    /**
     * Declared <code>synchronized</code>.
     * Used by Android dex.
     */
    ACC_DECLARED_SYNCHRONIZED(0x20000, "ACC_DECLARED_SYNCHRONIZED");

    /**
     * Binary value in the {@link ClassFile}.
     */
    public final long value;

    /**
     * Modifier in the java source file. Some modifier does not exist in the
     * source file but generated by compiler.
     */
    public final String modifier;

    /**
     * Modifiers for {@link ClassFile}.
     */
    public static final List<AccessFlag> ForClass = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * Modifiers for {@link field_info}.
     */
    public static final List<AccessFlag> ForField = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * Modifiers for {@link InnerClasses_attribute}.
     */
    public static final List<AccessFlag> ForInnerClass = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * Modifiers for {@link method_info}.
     */
    public static final List<AccessFlag> ForMethod = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * Modifiers for {@link MethodParameters_attribute}.
     */
    public static final List<AccessFlag> ForMethodParameters = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * {@link Module_attribute#module_flags} for {@link Module_attribute}.
     */
    public static final List<AccessFlag> ForModule = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * {@link Module_attribute.Exports#exports_flags} for
     * {@link Module_attribute.Exports}.
     */
    public static final List<AccessFlag> ForModuleExports = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * {@link Module_attribute.Opens#opens_flags} for
     * {@link Module_attribute.Opens}.
     */
    public static final List<AccessFlag> ForModuleOpens = Collections.synchronizedList(new ArrayList<AccessFlag>());
    /**
     * {@link Module_attribute.Requires#requires_flags} for
     * {@link Module_attribute.Requires}.
     */
    public static final List<AccessFlag> ForModuleRequires = Collections.synchronizedList(new ArrayList<AccessFlag>());

    static {
        // Access flags for a Class File
        AccessFlag.ForClass.add(ACC_PUBLIC);
        AccessFlag.ForClass.add(ACC_FINAL);
        AccessFlag.ForClass.add(ACC_SUPER);
        AccessFlag.ForClass.add(ACC_INTERFACE);
        AccessFlag.ForClass.add(ACC_ABSTRACT);
        AccessFlag.ForClass.add(ACC_SYNTHETIC);
        AccessFlag.ForClass.add(ACC_ANNOTATION);
        AccessFlag.ForClass.add(ACC_ENUM);
        AccessFlag.ForClass.add(ACC_MODULE);

        // Access flags for a Field
        AccessFlag.ForField.add(ACC_PUBLIC);
        AccessFlag.ForField.add(ACC_PRIVATE);
        AccessFlag.ForField.add(ACC_PROTECTED);
        AccessFlag.ForField.add(ACC_STATIC);
        AccessFlag.ForField.add(ACC_FINAL);
        AccessFlag.ForField.add(ACC_VOLATILE);
        AccessFlag.ForField.add(ACC_TRANSIENT);
        AccessFlag.ForField.add(ACC_SYNTHETIC);
        AccessFlag.ForField.add(ACC_ENUM);

        // Access flags for a Method
        AccessFlag.ForMethod.add(ACC_PUBLIC);
        AccessFlag.ForMethod.add(ACC_PRIVATE);
        AccessFlag.ForMethod.add(ACC_PROTECTED);
        AccessFlag.ForMethod.add(ACC_STATIC);
        AccessFlag.ForMethod.add(ACC_FINAL);
        AccessFlag.ForMethod.add(ACC_SYNCHRONIZED);
        AccessFlag.ForMethod.add(ACC_BRIDGE);
        AccessFlag.ForMethod.add(ACC_VARARGS);
        AccessFlag.ForMethod.add(ACC_NATIVE);
        AccessFlag.ForMethod.add(ACC_ABSTRACT);
        AccessFlag.ForMethod.add(ACC_STRICT);
        AccessFlag.ForMethod.add(ACC_SYNTHETIC);

        // Access flags for an Inner Class
        AccessFlag.ForInnerClass.add(ACC_PUBLIC);
        AccessFlag.ForInnerClass.add(ACC_PRIVATE);
        AccessFlag.ForInnerClass.add(ACC_PROTECTED);
        AccessFlag.ForInnerClass.add(ACC_STATIC);
        AccessFlag.ForInnerClass.add(ACC_FINAL);
        AccessFlag.ForInnerClass.add(ACC_INTERFACE);
        AccessFlag.ForInnerClass.add(ACC_ABSTRACT);
        AccessFlag.ForInnerClass.add(ACC_SYNTHETIC);
        AccessFlag.ForInnerClass.add(ACC_ANNOTATION);
        AccessFlag.ForInnerClass.add(ACC_ENUM);

        // Access flags for an MethodParameters
        AccessFlag.ForMethodParameters.add(ACC_FINAL);
        AccessFlag.ForMethodParameters.add(ACC_SYNTHETIC);
        AccessFlag.ForMethodParameters.add(ACC_MANDATED);

        // Access flags for a Module
        AccessFlag.ForModule.add(ACC_OPEN);
        AccessFlag.ForModule.add(ACC_SYNTHETIC);
        AccessFlag.ForModule.add(ACC_MANDATED);

        // Access flags for a Module.Exports
        AccessFlag.ForModuleExports.add(ACC_SYNTHETIC);
        AccessFlag.ForModuleExports.add(ACC_MANDATED);

        // Access flags for a Module.Opens
        AccessFlag.ForModuleOpens.add(ACC_SYNTHETIC);
        AccessFlag.ForModuleOpens.add(ACC_MANDATED);

        // Access flags for a Module.Requires
        AccessFlag.ForModuleRequires.add(ACC_TRANSITIVE);
        AccessFlag.ForModuleRequires.add(ACC_STATIC_PHASE);
        AccessFlag.ForModuleRequires.add(ACC_SYNTHETIC);
        AccessFlag.ForModuleRequires.add(ACC_MANDATED);
    }

    /**
     * @param i Value in the Class file
     * @param m Modifier in the java source file
     */
    private AccessFlag(long i, String m) {
        this.value = i;
        this.modifier = m;
    }

    /**
     * Get <code>int</code> value of {@link #value}.
     *
     * @return <code>int</code> value of {@link #value}
     */
    public int intValue() {
        return Math.toIntExact(this.value);
    }

    /**
     * Check if the the <code>accFlags</code> matches the access flag or not.
     *
     * @param accFlags the access flags value
     * @return <code>true</code> if the access flag matches the
     * <code>accFlags</code>, else <code>false</code>
     */
    public boolean match(long accFlags) {
        return (accFlags & this.value) > 0;
    }

    @Override
    public String toString() {
        return String.format("%s value=%s modifier=%s", name(), BytesTool.getBinaryString(this.value), this.modifier);
    }

    /**
     * Get the modifiers text for a {@link ClassFile}.
     *
     * @param value Value in the Class file
     * @return Modifier text
     */
    public static String getClassModifier(long value) {
        return getModifier(value, AccessFlag.ForClass);
    }

    /**
     * Get the modifiers text for a {@link field_info}.
     *
     * @param value Value in the Class file
     * @return Modifier text
     */
    public static String getFieldModifier(long value) {
        return getModifier(value, AccessFlag.ForField);
    }

    /**
     * Get the modifiers text for a {@link method_info}.
     *
     * @param value Value in the Class file
     * @return Modifier text
     */
    public static String getMethodModifier(long value) {
        return getModifier(value, AccessFlag.ForMethod);
    }

    /**
     * Get the modifiers text for a {@link InnerClasses_attribute}.
     *
     * @param value Value in the Class file
     * @return Modifier text
     */
    public static String getInnerClassModifier(long value) {
        return getModifier(value, AccessFlag.ForInnerClass);
    }

    /**
     * Get the modifiers text for a {@link MethodParameters_attribute}.
     *
     * @param value Value in the Class file
     * @return Modifier text
     */
    public static String getMethodParametersModifier(long value) {
        return getModifier(value, AccessFlag.ForMethodParameters);
    }

    public static String getModifier(long value, List<AccessFlag> list) {
        final List<String> modifiers = new ArrayList<>();
        list.stream().filter(flag -> (flag.match(value))).forEachOrdered(flag ->
            modifiers.add(flag.modifier)
        );

        return String.join(" ", modifiers);
    }
}
