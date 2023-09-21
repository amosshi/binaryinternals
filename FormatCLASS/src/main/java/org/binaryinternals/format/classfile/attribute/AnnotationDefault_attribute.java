/*
 * AttributeAnnotationDefault.java    11:35 AM, April 28, 2014
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
import org.binaryinternals.format.classfile.u2;

/**
 * The {@code AnnotationDefault} attribute is a variable-length attribute in the
 * {@code attributes} table of certain {@code method_info} structures, namely
 * those representing elements of annotation types. The
 * {@code AnnotationDefault} attribute records the default value for the element
 * represented by the {@code method_info} structure.
 *
 * The {@code AnnotationDefault} attribute has the following format:
 * <pre>
 * AnnotationDefault_attribute {
 *    u2            attribute_name_index;
 *    u4            attribute_length;
 *
 *    element_value default_value;
 * }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 5
 * @see <a
 * href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.22">
 * VM Spec: The AnnotationDefault attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class AnnotationDefault_attribute extends attribute_info {

    /**
     * The {@link #default_value} item represents the default value of the
     * annotation type element whose default value is represented by this
     * AnnotationDefault attribute.
     */
    public final Annotation.element_value default_value;

    AnnotationDefault_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.default_value = new Annotation.element_value(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        DefaultMutableTreeNode defaultValueNode = this.addNode(parentNode,
                super.startPos + 6,
                this.getLength() - 6,
                "default_value",
                "the default value of the annotation interface element represented by the method_info structure enclosing this AnnotationDefault attribute",
                "msg_attr_AnnotationDefault__default_value",
                Icons.Data
        );
        this.default_value.generateTreeNode(defaultValueNode, classFile);
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_AnnotationDefault";
    }
}
