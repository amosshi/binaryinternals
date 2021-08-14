/*
 * AttributeConstantValue.java    5:08 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code ModuleResolution} attribute.
 *
 * The {@code ModuleResolution} attribute has the following format:
 *
 * <pre>
 *    ModuleResolution_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        u2 resolution_flags;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 17
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class ModuleResolution_attribute extends attribute_info {

    /**
     * resolution_flags.
     */
    public final u2 resolution_flags;

    ModuleResolution_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        if (this.attribute_length.value != 2) {
            throw new FileFormatException(String.format("The attribute_length of ModuleResolution is not 2, it is %d.", this.attribute_length.value));
        }

        this.resolution_flags = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                2,
                "resolution_flags: " + BytesTool.getBinaryString(this.resolution_flags.value)
        )));
    }
}
