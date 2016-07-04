/*
 * SignatureConvertor.java    September 20, 2007, 11:10 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.util.ArrayList;
import java.util.List;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.AttributeSignature.ReferenceType;

/**
 * Convert the Internal Form of Names into java language specification type
 * names.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2">
 * VM Spec: The Internal Form of Names
 * </a>
 */
final public class SignatureConvertor {

    /**
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2.1">
     * VM Spec: Binary Class and Interface Names
     * </a>
     */
    public static final char BINARY_NAME_SEPARATOR = '/';

    /**
     * The ASCII periods (.) that normally separate the identifiers in in JLS (Java Language Specification).
     * 
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2.1">
     * VM Spec: Binary Class and Interface Names
     * </a>
     */
    public static final char JLS_NAME_SEPARATOR = '.';

    /**
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.3">
     * VM Spec: Method Descriptors
     * </a>
     */
    public static final char METHODDESCRIPTOR_LEFT = '(';
    /**
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.3">
     * VM Spec: Method Descriptors
     * </a>
     */
    public static final char METHODDESCRIPTOR_RIGHT = ')';

    /**
     * Get return type from method descriptor
     * {@link MethodInfo#descriptor_index}.
     *
     * @param signature Method signature in JVM internal format
     * @return Method return type in Java Programming language format
     * @throws FileFormatException Invalid signature string found
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.3">
     * VM Spec: Method Descriptors
     * </a>
     */
    public static SignatureResult MethodReturnTypeExtractor(final String signature)
            throws FileFormatException {
        if (signature == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        if (signature.length() < 3) {
            throw new IllegalArgumentException("'signature' should be more than 2 characters.");
        }

        final int bracketEnd = signature.indexOf(METHODDESCRIPTOR_RIGHT); // ')'
        if (bracketEnd == -1) {
            throw new IllegalArgumentException(String.format("There is no ')' in the method signature: %s", signature));
        }

        SignatureResult returnValue;
        final String returnType = signature.substring(bracketEnd + 1);
        if ("V".equals(returnType)) {
            returnValue = new SignatureResult(0, returnType, JavaLangSpec.Keyword.kw_void);
        } else {
            returnValue = SignatureConvertor.FieldDescriptorExtractor(returnType);
        }

        return returnValue;
    }

    /**
     * Get parameters type from method descriptor
     * {@link MethodInfo#descriptor_index}.
     * <p>
     * Example: <code>(ILjava/lang/String;[I)</code> to
     * <code>(int, String, int[])</code>
     * </p>
     *
     * @param signature JVM internal format of method signature
     * @return Java Language Specification (JLS) format of parameters
     * @throws FileFormatException Invalid signature string found
     */
    public static String MethodParameters2Readable(final String signature)
            throws FileFormatException {
        List<SignatureResult> paramters = MethodParametersSplit(signature);
        StringBuilder result = new StringBuilder();

        result.append(METHODDESCRIPTOR_LEFT);
        int size = paramters.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                result.append(paramters.get(i).toString());
                if (i != size - 1) {
                    result.append(',');
                }
            }
        }
        result.append(METHODDESCRIPTOR_RIGHT);

        return result.toString();
    }

    // (com/sun/java/accessibility/AccessBridge;Ljavax/accessibility/AccessibleContext;)V
    public static List<SignatureResult> MethodParametersSplit(final String signature)
            throws FileFormatException {
        // check parameter
        if (signature == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        if (signature.length() < 3) {
            throw new IllegalArgumentException("'signature' should be more than 2 characters.");
        }
        final int bracketEnd = signature.indexOf(METHODDESCRIPTOR_RIGHT);            // ')'
        if ((signature.charAt(0) != METHODDESCRIPTOR_LEFT) || (bracketEnd == -1)) {  // '('
            throw new IllegalArgumentException(String.format("There is no '(' or ')' in the method signature: %s", signature));
        }

        // get the parameter signatures: ILjava/lang/String;[I
        // if the first byte is
        //   '[': omited
        //   Primitive type; end
        //   'L': find the next ';', parse it; then end
        StringBuilder sbParameter = new StringBuilder();
        List<String> parameters = new ArrayList<>();
        List<SignatureResult> parametersResult = new ArrayList<>();

        String rawParameters = signature.substring(1, bracketEnd + 1);  // com/sun/java/accessibility/AccessBridge;Ljavax/accessibility/AccessibleContext;)
        while (rawParameters.charAt(0) != METHODDESCRIPTOR_RIGHT) {  // ')'
            if (rawParameters.charAt(0) == '[') {
                sbParameter.append(rawParameters.charAt(0));
                rawParameters = rawParameters.substring(1);
            } else if (AttributeSignature.BaseType.isPrimitiveType(rawParameters.charAt(0))) {
                sbParameter.append(rawParameters.charAt(0));
                rawParameters = rawParameters.substring(1);
                // Add one parameter
                parameters.add(sbParameter.toString());
                sbParameter.setLength(0);
                sbParameter.trimToSize();
            } else if (rawParameters.charAt(0) == ReferenceType.ClassTypeSignature.signature) {     // 'L'
                int commaIndex = rawParameters.indexOf(';');
                sbParameter.append(rawParameters.substring(0, commaIndex + 1));
                rawParameters = rawParameters.substring(commaIndex + 1);
                // Add one parameter
                parameters.add(sbParameter.toString());
                sbParameter.setLength(0);
                sbParameter.trimToSize();
            } else {
                throw new FileFormatException("Un-recognized method descriptor: " + signature);
            }
        }

        if (parameters.size() > 0) {
            for (String s : parameters) {
                parametersResult.add(FieldDescriptorExtractor(s));
            }
        }

        return parametersResult;
    }

    /**
     * Extract field descriptor. Example:
     * <pre>
     * B                   -- byte
     * [I                  -- int[]
     * Ljava/lang/Object;  -- java.lang.Object
     * [Ljava/lang/String; -- java.lang.String[]
     * </pre>
     *
     * @param signature JVM internal format of field signature
     * @return Signature Parse result
     * @throws FileFormatException Invalid signature string found
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.2">
     * VM Spec: Field Descriptors
     * </a>
     */
    public static SignatureResult FieldDescriptorExtractor(final String signature)
            throws FileFormatException {

        if ((signature == null) || signature.isEmpty()) {
            throw new IllegalArgumentException("'signature' should not be null or empty.");
        }

        String sig = signature;
        int arrayCount = 0;
        while (sig.charAt(0) == ReferenceType.ArrayTypeSignature.signature) {  // '['
            arrayCount++;
            sig = sig.substring(1);
        }

        //
        final int sigLength = sig.length();
        String sigJls;
        if (sigLength == 1) {
            sigJls = AttributeSignature.BaseType.extractPrimitiveType(sig.charAt(0));
        } else {
            if (sig.charAt(0) == ReferenceType.ClassTypeSignature.signature
                    && sig.charAt(sig.length() - 1) == ReferenceType.ClassTypeSignatureSuffix.signature) {
                sig = sig.substring(1, sigLength - 1);
            }

            sigJls = ParseClassSignature(sig);
        }

        return new SignatureResult(arrayCount, sig, sigJls);
    }

    /**
     * Convert class signature from JVM internal format to Java programming
     * language format.
     * <p>
     * Example: convert <code>java/lang/String;</code> to
     * <code>java.lang.String</code>.
     * </p>
     *
     * @param classSignature JVM internal format of class signature
     * @return Java Language Specification (JLS) format of class signature
     */
    public static String ParseClassSignature(final String classSignature)
            throws IllegalArgumentException {
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }

        return classSignature.replace(SignatureConvertor.BINARY_NAME_SEPARATOR, SignatureConvertor.JLS_NAME_SEPARATOR);
    }

    /**
     * Set package name of the class signature.
     * 
     * @param classSignature JVM internal format of class signature
     * @return Package name of class signature, or null if the class not in any package
     */
    public static String ParsePackage(final String classSignature){
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }
        
        int lastIndex = classSignature.lastIndexOf(SignatureConvertor.BINARY_NAME_SEPARATOR);
        if (lastIndex == -1) {
            return null;
        } else {
            return SignatureConvertor.ParseClassSignature(classSignature.substring(0, lastIndex)).toLowerCase();
        }
    }

    public static class SignatureResult {

        /**
         * Dimension if it is an array.
         */
        public final int ArrayDimension;
        /**
         * Binary name.
         */
        public final String TypeBinaryName;
        /**
         * Parsed Java Language Specification type name.
         */
        public final String TypeJLSName;
        
        /**
         * Package name of the type. It will be <code>null</code> if the {@link #TypeJLSName} do not in a package.
         */
        public final String TypePackage;

        SignatureResult(int count, String bin, String jls) {
            this.ArrayDimension = count;
            this.TypeBinaryName = bin;
            this.TypeJLSName = jls;
            this.TypePackage = ParsePackage(bin);
        }

        /**
         * Get the readable format of signature.
         *
         * @return Readable format of signature
         */
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
