/*
 * AttributeSourceFile.java    5:26 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code SourceFile} attribute. The {@code SourceFile}
 * attribute has the following format:
 *
 * <pre>
 *    SourceFile_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 sourcefile_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.10">
 * VM Spec: The SourceFile Attribute
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class SourceFile_attribute extends attribute_info {

    public final u2 sourcefile_index;

    SourceFile_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);

        if (this.attribute_length.value != 2) {
            throw new FileFormatException(String.format("The attribute_length of AttributeSourceFile is not 2, it is %d.", this.attribute_length.value));
        }

        this.sourcefile_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        int cpIndex = this.sourcefile_index.value;
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                String.format("sourcefile_index: %d %s", cpIndex, classFile.getCPDescription(cpIndex))
        )));
    }

}