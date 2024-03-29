package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 * The RuntimeVisibleTypeAnnotations attribute records run-time visible
 * annotations on types used in the declaration of the corresponding class,
 * field, or method, or in an expression in the corresponding method body. The
 * RuntimeVisibleTypeAnnotations attribute also records run-time visible
 * annotations on type parameter declarations of generic classes, interfaces,
 * methods, and constructors. The Java Virtual Machine must make these
 * annotations available so they can be returned by the appropriate reflective
 * APIs.
 *
 * The RuntimeVisibleTypeAnnotations attribute has the following format:
 * <pre>
 *   RuntimeVisibleTypeAnnotations_attribute {
 *     u2              attribute_name_index;
 *     u4              attribute_length;
 *
 *     u2              num_annotations;
 *     type_annotation annotations[num_annotations];
 *   }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 8
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.20">
 * VM Spec: The RuntimeVisibleTypeAnnotations Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeVisibleTypeAnnotations_attribute extends RuntimeTypeAnnotations {

    public RuntimeVisibleTypeAnnotations_attribute(u2 nameIndex, String name, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, name, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeVisibleTypeAnnotations";
    }

    @Override
    String getMessageKey_4_annotations() {
        return "msg_attr_RuntimeVisibleTypeAnnotations__annotations";
    }

    @Override
    String getMessageKey_4_num_annotations() {
        return "msg_attr_RuntimeVisibleTypeAnnotations__num_annotations";
    }
}
