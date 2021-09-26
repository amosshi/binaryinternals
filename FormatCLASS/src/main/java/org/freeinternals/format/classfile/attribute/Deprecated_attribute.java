/*
 * AttributeDeprecated.java    5:37 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code Deprecated} attribute. The {@code Deprecated}
 * attribute has the following format:
 *
 * <pre>
 *    Deprecated_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.1
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.7.15">
 * VM Spec: The Deprecated Attribute
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Deprecated_attribute extends attribute_info {

    public static final int LENGTH = 0;

    Deprecated_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != LENGTH) {
            throw new FileFormatException(String.format("The attribute_length of Deprecated is not %d, it is %d.", LENGTH, this.attribute_length.value));
        }

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        // Nothing to add
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_Deprecated";
    }
}
