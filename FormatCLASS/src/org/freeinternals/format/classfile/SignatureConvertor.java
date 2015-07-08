/*
 * SignatureConvertor.java    September 20, 2007, 11:10 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
final public class SignatureConvertor {

    /**
     * Get return type from method descriptor
     * {@link MethodInfo#descriptor_index}.
     *
     * @param signature Method signature in JVM internal format
     * @return Method return type in Java Programming language format
     * @throws org.freeinternals.format.classfile.SignatureException Invalid
     * signature string found
     */
    public static String parseMethodReturnType(final String signature)
            throws SignatureException {
        if (signature == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        if (signature.length() < 3) {
            throw new IllegalArgumentException("'signature' should be more than 2 characters.");
        }

        final int bracketEnd = signature.indexOf(')');
        if (bracketEnd == -1) {
            throw new IllegalArgumentException(String.format("There is no ')' in the method signature: %s", signature));
        }

        String returnValue;
        final String returnType = signature.substring(bracketEnd + 1);
        if ("V".equals(returnType)) {
            returnValue = JavaLangSpec.Keyword.kw_void;
        } else {
            returnValue = SignatureConvertor.signature2Type(returnType);
        }

        return returnValue;
    }

    // 
    /**
     * Get parameters type from method descriptor
     * {@link MethodInfo#descriptor_index}.
     * <p>
     * Example: <code>(ILjava/lang/String;[I)</code>   -->  <code>(int, String, int[])</code>
     * </p>
     *
     * @param signature
     * @return Method parameters in Java Programming language format
     * @throws org.freeinternals.format.classfile.SignatureException Invalid
     * signature string found
     */
    public static String parseMethodParameters(final String signature)
            throws SignatureException {
        // check parameter
        if (signature == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }
        if (signature.length() < 3) {
            throw new IllegalArgumentException("'signature' should be more than 2 characters.");
        }
        final int bracketEnd = signature.indexOf(')');
        if ((signature.charAt(0) != '(') || (bracketEnd == -1)) {
            throw new IllegalArgumentException(String.format("There is no '(' or ')' in the method signature: %s", signature));
        }

        // get the parameter signatures: ILjava/lang/String;[I
        // if the first byte is
        //   '[': omited
        //   Primitive type; end
        //   'L': find the next ';', parse it; then end
        final StringBuilder sbResult = new StringBuilder(signature.length() + signature.length());
        sbResult.append('(');

        StringBuilder sbParameter = new StringBuilder(sbResult.capacity());
        int commaIndex;

        String rawParameters = signature.substring(1, bracketEnd + 1);
        int parametersCounter = 0;
        while (rawParameters.charAt(0) != ')') {
            if (rawParameters.charAt(0) == '[') {
                sbParameter.append("[]");
                rawParameters = rawParameters.substring(1);
            } else {
                if (AttributeSignature.BaseType.isPrimitiveType(rawParameters.charAt(0))) {
                    sbParameter = sbParameter.insert(0, AttributeSignature.BaseType.extractPrimitiveType(rawParameters.charAt(0)));
                    sbResult.append(sbParameter);
                    parametersCounter++;
                    sbParameter = sbParameter.delete(0, sbParameter.capacity());

                    rawParameters = rawParameters.substring(1);
                } else if (rawParameters.charAt(0) == 'L') {
                    commaIndex = rawParameters.indexOf(';');
                    sbParameter = sbParameter.insert(0, SignatureConvertor.extractClassFullSignature(
                            rawParameters.substring(0, commaIndex + 1)));
                    sbResult.append(sbParameter);
                    parametersCounter++;
                    sbParameter = sbParameter.delete(0, sbParameter.capacity());

                    rawParameters = rawParameters.substring(commaIndex + 1);
                }

                sbResult.append(", ");
            }
        }
        if (parametersCounter > 0) {
            sbResult.delete(sbResult.length() - 2, sbResult.length());
        }

        sbResult.append(')');
        return sbResult.toString();
    }

    /**
     * Convert field signature from JVM internal format
     * ({@link FieldInfo#descriptor_index} text) to Java programming language
     * format.
     * <p>
     * Its format is <code>[[[ + type</code>.</p>
     * <p>
     * Used to analysis the field type.</p>
     *
     * @param signature JVM internal format of field signature
     * @return Java programming language format of field signature
     * @throws org.freeinternals.format.classfile.SignatureException Invalid
     * signature string found
     */
    public static String signature2Type(final String signature)
            throws SignatureException {
        if ((signature == null) || signature.isEmpty()) {
            throw new IllegalArgumentException("'signature' should not be null or empty.");
        }

        final StringBuilder sb = new StringBuilder(signature.length());
        String sig = signature;
        int arrayCount = 0;
        while (sig.charAt(0) == '[') {
            arrayCount++;
            sig = sig.substring(1);
        }

        sb.append(SignatureConvertor.rawSignature2Type(sig));
        while (arrayCount > 0) {
            sb.append("[]");
            arrayCount--;
        }

        return sb.toString();
    }

    // Primitive types;
    //   and
    // L full-qualified-class ;
    private static String rawSignature2Type(final String signature)
            throws IllegalArgumentException, SignatureException {
        if (signature == null) {
            throw new IllegalArgumentException("'signature' should not be null.");
        }

        String returnValue;
        final int signatureLength = signature.length();
        switch (signatureLength) {
            case 0:
            case 2:
                throw new SignatureException(String.format("Sinagure length cannot be 0, or 2. Current 'signature' length=%d.", signatureLength));

            case 1:
                returnValue = AttributeSignature.BaseType.extractPrimitiveType(signature.charAt(0));
                break;

            default:
                returnValue = extractClassFullSignature(signature);
        }

        return returnValue;
    }

    // L full-qualified-class ;
    // Ljava/lang/String;  --> java.lang.String
    private static String extractClassFullSignature(final String classSignature)
            throws IllegalArgumentException, SignatureException {
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }

        final int length = classSignature.length();
        if (length < 3) {
            throw new SignatureException(String.format("Fully-qualified class sinagure length cannot be less than 2. Current length=%d.", length));
        }
        if ((classSignature.charAt(0) != 'L') || (classSignature.charAt(length - 1) != ';')) {
            throw new SignatureException(String.format("Fully-qualified class sinagure format is not 'L-xxx-;'. it is '%s'.", classSignature));
        }

        String returnValue = classSignature.substring(1, length - 1);
        return returnValue.replace('/', '.');
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
     * @return Java programming language format of class signature
     */
    public static String parseClassSignature(final String classSignature)
            throws IllegalArgumentException {
        if (classSignature == null) {
            throw new IllegalArgumentException("'ClassSignature' should not be null.");
        }

        return classSignature.replace('/', '.');
    }
}
