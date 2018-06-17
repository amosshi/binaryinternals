/*
 * AttributeSignature.java    10:52 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 * An optional fixed-length attribute in the attributes table of a
 * {@code ClassFile}, {@code field_info}, or {@code method_info} structure.
 *
 * @author Amos Shi
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.9">
 * VM Spec: The Signature Attribute
 * </a>
 */
public class AttributeSignature extends AttributeInfo {

    public transient final u2 signature_index;

    AttributeSignature(
            final u2 nameIndex,
            final String type,
            final PosDataInputStream posDataInputStream,
            final CPInfo[] cp)
            throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.signature_index = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    /**
     * A primitive type of the Java programming language.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-BaseType">
     * VM Spec: BaseType
     * </a>
     */
    public enum BaseType {

        /**
         * signed byte.
         */
        B('B', JavaLangSpec.Keyword.kw_byte),
        /**
         * Unicode character code point in the Basic Multilingual Plane, encoded
         * with UTF-16.
         */
        C('C', JavaLangSpec.Keyword.kw_char),
        /**
         * double-precision floating-point value.
         */
        D('D', JavaLangSpec.Keyword.kw_double),
        /**
         * single-precision floating-point value.
         */
        F('F', JavaLangSpec.Keyword.kw_float),
        /**
         * integer.
         */
        I('I', JavaLangSpec.Keyword.kw_int),
        /**
         * long integer.
         */
        J('J', JavaLangSpec.Keyword.kw_long),
        /**
         * signed short.
         */
        S('S', JavaLangSpec.Keyword.kw_short),
        /**
         * true or false.
         */
        Z('Z', JavaLangSpec.Keyword.kw_boolean);

        public final char signature;
        public final String JavaKeyWord;

        private BaseType(char c, String kw) {
            this.signature = c;
            this.JavaKeyWord = kw;
        }

        /**
         * Get the key word of the JVM internal type signature char.
         *
         * @param typeSignature JVM internal type signature char
         * @return the key word or <code>error message</code> if not found
         */
        public static String extractPrimitiveType(char typeSignature) {
            String kw = "[ERROR: unknown primitive type]";
            for (BaseType v : BaseType.values()) {
                if (v.signature == typeSignature) {
                    kw = v.JavaKeyWord;
                    break;
                }
            }

            return kw;
        }

        /**
         * Check if a JVM internal type signature is primitive type or not.
         *
         * @param typeSignature JVM internal type signature char
         * @return <code>true</code> for primitive types, else
         * <code>false</code>
         */
        public static Boolean isPrimitiveType(final char typeSignature) {
            Boolean returnValue = false;
            for (BaseType v : BaseType.values()) {
                if (v.signature == typeSignature) {
                    returnValue = true;
                    break;
                }
            }

            return returnValue;
        }
    }

    /**
     * A reference type signature represents a reference type of the Java
     * programming language, that is, a class or interface type, a type
     * variable, or an array type.
     *
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.9.1">
     * VM Spec: Reference Type Signature
     * </a>
     */
    public enum ReferenceType {

        ClassTypeSignature('L'),
        ClassTypeSignatureSuffix(';'),
        TypeVariableSignature('T'),
        ArrayTypeSignature('[');

        public final char signature;

        private ReferenceType(char c) {
            this.signature = c;
        }

    }
}
