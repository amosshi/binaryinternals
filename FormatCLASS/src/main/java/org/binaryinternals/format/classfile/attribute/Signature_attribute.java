/*
 * AttributeSignature.java    10:52 AM, April 28, 2014
 *
 * Copyright  2014, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.JavaLangSpec;
import org.binaryinternals.format.classfile.u2;

/// An optional fixed-length attribute in the attributes table of a
/// `ClassFile`, `field_info`, or `method_info` structure.
///
/// @author Amos Shi
/// @since Java 5
/// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.9">
/// VM Spec: The Signature Attribute
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class Signature_attribute extends attribute_info {

    /// Constant pool index of a `CONSTANT_Utf8_info` with a class, method or field signature.
    public final u2 signature_index;

    Signature_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException {
        super(nameIndex, type, posDataInputStream);
        this.signature_index = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int sigIndex = this.signature_index.value;

        this.addNode(parentNode, super.startPos + 6, 2,
                "signature_index",
                TEXT_CPINDEX_VALUE.formatted(sigIndex, "signature", ((ClassFile) classFile).getCPDescription(sigIndex)),
                "msg_attr_Signature__signature_index",
                Icons.Signature
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Signature";
    }

    /// A primitive type of the Java programming language.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-BaseType">
    /// VM Spec: BaseType
    /// </a>
    public enum BaseType {

        /// signed byte.
        B('B', JavaLangSpec.Keyword.BYTE.text),
        /// Unicode character code point in the Basic Multilingual Plane, encoded
        /// with UTF-16.
        C('C', JavaLangSpec.Keyword.CHAR.text),
        /// double-precision floating-point value.
        D('D', JavaLangSpec.Keyword.DOUBLE.text),
        /// single-precision floating-point value.
        F('F', JavaLangSpec.Keyword.FLOAT.text),
        /// integer.
        I('I', JavaLangSpec.Keyword.INT.text),
        /// long integer.
        J('J', JavaLangSpec.Keyword.LONG.text),
        /// signed short.
        S('S', JavaLangSpec.Keyword.SHORT.text),
        /// true or false.
        Z('Z', JavaLangSpec.Keyword.BOOLEAN.text);

        /// The signature character.
        public final char signature;
        /// The Java keyword of the base type.
        public final String JavaKeyWord;

        BaseType(char c, String kw) {
            this.signature = c;
            this.JavaKeyWord = kw;
        }

        /// Get the key word of the JVM internal type signature char.
        ///
        /// @param typeSignature JVM internal type signature char
        /// @return the key word or `error message` if not found
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

        /// Check if a JVM internal type signature is primitive type or not.
        ///
        /// @param typeSignature JVM internal type signature char
        /// @return `true` for primitive types, else
        /// `false`
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

    /// A reference type signature represents a reference type of the Java
    /// programming language, that is, a class or interface type, a type
    /// variable, or an array type.
    ///
    /// @see <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.7.9.1">
    /// VM Spec: Reference Type Signature
    /// </a>
    ///
    /// ```
    /// java:S115 - Constant names should comply with a naming convention --- We respect the name from JVM Spec instead
    /// ```
    @SuppressWarnings("java:S115")
    public enum ReferenceType {

        /// Class type signature start, `L`.
        ClassTypeSignature('L'),
        /// Class type signature end, `;`.
        ClassTypeSignatureSuffix(';'),
        /// Type variable signature start, `T`.
        TypeVariableSignature('T'),
        /// Array type signature start, `[`.
        ArrayTypeSignature('[');

        /// The signature character.
        public final char signature;

        ReferenceType(char c) {
            this.signature = c;
        }

    }
}
