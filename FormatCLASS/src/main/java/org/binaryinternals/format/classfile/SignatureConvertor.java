/*
 * SignatureConvertor.java    September 20, 2007, 11:10 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.stream.Collectors;
import org.binaryinternals.commonlib.core.FileFormatException;

/// Convert the Internal Form of Names into java language specification type
/// names.
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.2">
/// VM Spec: The Internal Form of Names
/// </a>
///
/// ```
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings("java:S116")
public final class SignatureConvertor {

    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.2.1">
    /// VM Spec: Binary Class and Interface Names
    /// </a>
    public static final char BINARY_NAME_SEPARATOR = '/';

    /// The ASCII periods (.) that normally separate the identifiers in in JLS (Java Language Specification).
    ///
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.2.1">
    /// VM Spec: Binary Class and Interface Names
    /// </a>
    public static final char JLS_NAME_SEPARATOR = '.';

    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.3.3">
    /// VM Spec: Method Descriptors
    /// </a>
    public static final char METHODDESCRIPTOR_LEFT = '(';
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.3.3">
    /// VM Spec: Method Descriptors
    /// </a>
    public static final char METHODDESCRIPTOR_RIGHT = ')';
    /// `void` return type for method.
    public static final String METHODRETURN_VOID = "V";

    private SignatureConvertor() {
    }

    /// Get return type from method descriptor {@link method_info#descriptor_index}.
    ///
    /// @param signature Method signature in JVM internal format
    /// @return Method return type in Java Programming language format
    /// @throws FileFormatException Invalid signature string found
    /// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.3.3">
    /// VM Spec: Method Descriptors
    /// </a>
    public static SignatureResult methodReturnTypeExtractor(final String signature) throws FileFormatException {
        return toResult(methodType(signature).returnType());
    }

    /// JLS format of return type.
    ///
    /// @param returnType Return type in binary format, which is a field
    /// descriptor or `V`
    /// @return Method return type in Java Programming language format
    /// @throws FileFormatException Invalid signature string found
    public static SignatureResult methodReturnTypeJLS(final String returnType) throws FileFormatException {
        return toResult(classDesc(returnType));
    }

    /// Get parameters type from method descriptor {@link method_info#descriptor_index}.
    ///
    ///
    /// Example: `(ILjava/lang/String;[I)` to
    /// `(int,java.lang.String,int[])`
    ///
    ///
    /// @param signature JVM internal format of method signature
    /// @return Java Language Specification (JLS) format of parameters
    /// @throws FileFormatException Invalid signature string found
    public static String methodParameters2Readable(final String signature) throws FileFormatException {
        return methodParametersSplit(signature).stream()
                .map(SignatureResult::toString)
                .collect(Collectors.joining(",", String.valueOf(METHODDESCRIPTOR_LEFT), String.valueOf(METHODDESCRIPTOR_RIGHT)));
    }

    /// Get the parameter types of a method descriptor, for example
    /// `(Lcom/sun/java/accessibility/AccessBridge;I)V`.
    ///
    /// @param signature JVM internal format of method signature
    /// @return The parameter types
    /// @throws FileFormatException Invalid signature string found
    public static List<SignatureResult> methodParametersSplit(final String signature) throws FileFormatException {
        return methodType(signature).parameterList().stream()
                .map(SignatureConvertor::toResult)
                .toList();
    }

    /// Extract field descriptor. Example:
    /// ```
    /// B                   -- byte
    /// [I                  -- int[]
    /// Ljava/lang/Object;  -- java.lang.Object
    /// [Ljava/lang/String; -- java.lang.String[]
    /// ```
    ///
    /// @param signature JVM internal format of field signature
    /// @return Signature Parse result
    /// @throws FileFormatException Invalid signature string found
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.3.2">
    /// VM Spec: Field Descriptors
    /// </a>
    public static SignatureResult fieldDescriptorExtractor(final String signature) throws FileFormatException {
        return toResult(classDesc(signature));
    }

    private static MethodTypeDesc methodType(final String descriptor) throws FileFormatException {
        if (descriptor == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        try {
            return MethodTypeDesc.ofDescriptor(descriptor);
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new FileFormatException("Un-recognized method descriptor: " + descriptor, ex);
        }
    }

    private static ClassDesc classDesc(final String descriptor) throws FileFormatException {
        if (descriptor == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        try {
            return ClassDesc.ofDescriptor(descriptor);
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new FileFormatException("Un-recognized field descriptor: " + descriptor, ex);
        }
    }

    private static SignatureResult toResult(final ClassDesc type) {
        int arrayDimension = 0;
        ClassDesc component = type;
        while (component.isArray()) {
            arrayDimension++;
            component = component.componentType();
        }

        if (component.isPrimitive()) {
            return new SignatureResult(arrayDimension, component.descriptorString(), component.displayName());
        }
        final String descriptor = component.descriptorString();
        final String binaryName = descriptor.substring(1, descriptor.length() - 1);  // Strip the 'L' and ';'
        return new SignatureResult(arrayDimension, binaryName, parseClassSignature(binaryName));
    }

    /// Convert class signature from JVM internal format to Java programming
    /// language format.
    ///
    /// Example: convert `java/lang/String;` to
    /// `java.lang.String`.
    ///
    ///
    /// @param classSignature JVM internal format of class signature
    /// @return Java Language Specification (JLS) format of class signature
    public static String parseClassSignature(final String classSignature)
            throws IllegalArgumentException {
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }

        return classSignature.replace(SignatureConvertor.BINARY_NAME_SEPARATOR, SignatureConvertor.JLS_NAME_SEPARATOR);
    }

    /// Set package name of the class signature.
    ///
    /// @param classSignature JVM internal format of class signature
    /// @return Package name of class signature, or null if the class not in any package
    public static String parsePackage(final String classSignature){
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }
        
        int lastIndex = classSignature.lastIndexOf(SignatureConvertor.BINARY_NAME_SEPARATOR);
        if (lastIndex == -1) {
            return null;
        } else {
            return SignatureConvertor.parseClassSignature(classSignature.substring(0, lastIndex));
        }
    }

    /// Signature parse result.
    public static class SignatureResult {

        /// Dimension if it is an array.
        public final int ArrayDimension;
        /// Binary name.
        public final String TypeBinaryName;
        /// Parsed Java Language Specification type name.
        public final String TypeJLSName;
        
        /// Package name of the type. It will be `null` if the {@link #TypeJLSName} do not in a package.
        public final String TypePackage;

        SignatureResult(int count, String bin, String jls) {
            this.ArrayDimension = count;
            this.TypeBinaryName = bin;
            this.TypeJLSName = jls;
            this.TypePackage = parsePackage(bin);
        }

        /// Get the readable format of signature.
        ///
        /// @return Readable format of signature
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            int count = this.ArrayDimension;
            sb.append(this.TypeJLSName);
            while (count > 0) {
                sb.append("[]");
                count--;
            }

            return sb.toString();
        }
    }
}
