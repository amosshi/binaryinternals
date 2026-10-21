/*
 * SignatureConvertor.java    September 20, 2007, 11:10 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Objects;
import java.util.stream.Collectors;
import org.binaryinternals.commonlib.FileFormatException;

/// Convert field and method descriptors into Java language type names, for
/// example `[[Ljava/lang/String;` into `java.lang.String[][]`.
///
/// The descriptors are parsed by {@link ClassDesc} and {@link MethodTypeDesc};
/// this class formats them with the fully qualified names, while
/// {@link ClassDesc#displayName()} only gives the simple names.
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.3">
/// VM Spec: Descriptors
/// </a>
public final class SignatureConvertor {

    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.2.1">
    /// VM Spec: Binary Class and Interface Names
    /// </a>
    public static final char BINARY_NAME_SEPARATOR = '/';

    private SignatureConvertor() {
    }

    /// Get the Java type of a field descriptor.
    ///
    /// @param descriptor Field descriptor, for example `[[Ljava/lang/String;`
    /// @return Java type, for example `java.lang.String[][]`
    /// @throws FileFormatException Invalid descriptor
    public static String fieldType(final String descriptor) throws FileFormatException {
        try {
            return javaName(ClassDesc.ofDescriptor(Objects.requireNonNull(descriptor, "descriptor")));
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new FileFormatException("Un-recognized field descriptor: " + descriptor, ex);
        }
    }

    /// Get the Java parameter types of a method descriptor.
    ///
    /// @param descriptor Method descriptor, for example `(ILjava/lang/String;)V`
    /// @return Java parameter types, for example `(int,java.lang.String)`
    /// @throws FileFormatException Invalid descriptor
    public static String methodParameters(final String descriptor) throws FileFormatException {
        return methodType(descriptor).parameterList().stream()
                .map(SignatureConvertor::javaName)
                .collect(Collectors.joining(",", "(", ")"));
    }

    /// Get the Java return type of a method descriptor.
    ///
    /// @param descriptor Method descriptor, for example `()[Ljava/lang/Object;`
    /// @return Java return type, for example `java.lang.Object[]`
    /// @throws FileFormatException Invalid descriptor
    public static String methodReturnType(final String descriptor) throws FileFormatException {
        return javaName(methodType(descriptor).returnType());
    }

    /// Convert a binary class name into the Java name.
    ///
    /// @param binaryName Binary name, for example `java/util/Map$Entry`
    /// @return Java name, for example `java.util.Map$Entry`
    public static String javaName(final String binaryName) {
        return Objects.requireNonNull(binaryName, "binaryName").replace(BINARY_NAME_SEPARATOR, '.');
    }

    private static MethodTypeDesc methodType(final String descriptor) throws FileFormatException {
        try {
            return MethodTypeDesc.ofDescriptor(Objects.requireNonNull(descriptor, "descriptor"));
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new FileFormatException("Un-recognized method descriptor: " + descriptor, ex);
        }
    }

    private static String javaName(final ClassDesc type) {
        if (type.isArray()) {
            return javaName(type.componentType()) + "[]";
        }
        if (type.isPrimitive()) {
            return type.displayName();
        }
        final String descriptor = type.descriptorString();
        return javaName(descriptor.substring(1, descriptor.length() - 1));  // Strip the 'L' and ';'
    }
}
