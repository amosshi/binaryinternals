/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
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
import org.binaryinternals.format.classfile.u2;

/**
 *
 * The class for the {@code NestHost} attribute. The {@code NestHost} attribute
 * has the following format:
 *
 * <pre>
 *    NestHost_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 host_class_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 11
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.28">
 * VM Spec: The NestHost Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class NestHost_attribute extends attribute_info {

    /**
     * The value of the {@link #host_class_index} item must be a valid index
     * into the {@link ClassFile#constant_pool} table. The
     * {@link ClassFile#constant_pool} entry at that index must be a
     * CONSTANT_Class_info structure representing a class or interface which is
     * the nest host for the current class or interface.
     */
    public final u2 host_class_index;

    NestHost_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.host_class_index = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int startPosMoving = super.startPos + 6;

        int cpIndex = this.host_class_index.value;
        this.addNode(parentNode,
                startPosMoving, u2.LENGTH,
                "host_class_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "host class", ((ClassFile)classFile).getCPDescription(cpIndex)),
                "msg_attr_NestHost__host_class_index", Icons.Class
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_NestHost";
    }
}
