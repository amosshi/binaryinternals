/*
 * AttributeAnnotationDefault.java    11:35 AM, April 28, 2014
 *
 * Copyright  2014, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

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
 * href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.22">
 * VM Spec: The AnnotationDefault attribute
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class AttributeAnnotationDefault extends AttributeInfo {

    /**
     * The {@link #default_value} item represents the default value of the
     * annotation type element whose default value is represented by this
     * AnnotationDefault attribute.
     */
    public final Annotation.ElementValue default_value;

    AttributeAnnotationDefault(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws java.io.IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream, ClassFile.Version.FORMAT_49_0, JavaSEVersion.VERSION_5_0);
        this.default_value = new Annotation.ElementValue(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        DefaultMutableTreeNode defaultValueNode = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                this.getLength() - 6,
                "default_value"
        ));
        parentNode.add(defaultValueNode);
        
        Annotation.generateSubnode(defaultValueNode, this.default_value, classFile);
    }
}
