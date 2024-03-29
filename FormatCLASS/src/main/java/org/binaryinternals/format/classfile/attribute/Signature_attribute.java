/*
 * AttributeSignature.java    10:52 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.JavaLangSpec;
import org.binaryinternals.format.classfile.constant.cp_info;
import org.binaryinternals.format.classfile.u2;

/**
 * An optional fixed-length attribute in the attributes table of a
 * {@code ClassFile}, {@code field_info}, or {@code method_info} structure.
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.9">
 * VM Spec: The Signature Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class Signature_attribute extends attribute_info {

    public final u2 signature_index;

    /**
     * <pre>
     * java:S1172 - Unused method parameters should be removed --- `cp` is used by children classes
     * </pre>
     */
    @SuppressWarnings("java:S1172")
    Signature_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream, final cp_info[] cp) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.signature_index = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int sigIndex = this.signature_index.value;

        this.addNode(parentNode, super.startPos + 6, 2,
                "signature_index",
                String.format(TEXT_CPINDEX_VALUE, sigIndex, "signature", ((ClassFile) classFile).getCPDescription(sigIndex)),
                "msg_attr_Signature__signature_index",
                Icons.Signature
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Signature";
    }

    /**
     * A primitive type of the Java programming language.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-BaseType">
     * VM Spec: BaseType
     * </a>
     */
    public enum BaseType {

        /**
         * signed byte.
         */
        B('B', JavaLangSpec.Keyword.BYTE.text),
        /**
         * Unicode character code point in the Basic Multilingual Plane, encoded
         * with UTF-16.
         */
        C('C', JavaLangSpec.Keyword.CHAR.text),
        /**
         * double-precision floating-point value.
         */
        D('D', JavaLangSpec.Keyword.DOUBLE.text),
        /**
         * single-precision floating-point value.
         */
        F('F', JavaLangSpec.Keyword.FLOAT.text),
        /**
         * integer.
         */
        I('I', JavaLangSpec.Keyword.INT.text),
        /**
         * long integer.
         */
        J('J', JavaLangSpec.Keyword.LONG.text),
        /**
         * signed short.
         */
        S('S', JavaLangSpec.Keyword.SHORT.text),
        /**
         * true or false.
         */
        Z('Z', JavaLangSpec.Keyword.BOOLEAN.text);

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
     * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.9.1">
     * VM Spec: Reference Type Signature
     * </a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
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
