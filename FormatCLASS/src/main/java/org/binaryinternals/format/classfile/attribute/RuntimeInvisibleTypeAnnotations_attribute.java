package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/**
 * The RuntimeInvisibleTypeAnnotations attribute records run-time invisible
 * annotations on types used in the corresponding declaration of a class, field,
 * or method, or in an expression in the corresponding method body. The
 * RuntimeInvisibleTypeAnnotations attribute also records annotations on type
 * parameter declarations of generic classes, interfaces, methods, and
 * constructors.
 *
 * The RuntimeInvisibleTypeAnnotations attribute has the following format:
 * <pre>
 *   RuntimeInvisibleTypeAnnotations_attribute {
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
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.21">
 * VM Spec: The RuntimeInvisibleTypeAnnotations Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class RuntimeInvisibleTypeAnnotations_attribute extends RuntimeTypeAnnotations {

    public RuntimeInvisibleTypeAnnotations_attribute(u2 nameIndex, String name, PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, name, posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_RuntimeInvisibleTypeAnnotations";
    }

    @Override
    String getMessageKey_4_annotations() {
        return "msg_attr_RuntimeInvisibleTypeAnnotations__annotations";
    }

    @Override
    String getMessageKey_4_num_annotations() {
        return "msg_attr_RuntimeInvisibleTypeAnnotations__num_annotations";
    }
}
