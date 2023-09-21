/*
 * AttributeSynthetic.java    5:25 AM, August 5, 2007
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
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code SourceFile} attribute. The {@code SourceFile}
 * attribute has the following format:
 *
 * <pre>
 *    Synthetic_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.1
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.8">
 * VM Spec: The Synthetic Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Synthetic_attribute extends attribute_info {

    Synthetic_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 0) {
            throw new FileFormatException(String.format("The attribute_length of AttributeSynthetic is not 0, it is %d.", this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        // Nothing to add
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Synthetic";
    }
}
