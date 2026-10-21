/*
 * AttributeConstantValue.java    5:08 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.attribute;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.BytesTool;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.classfile.u2;

/// The class for the `ModuleResolution` attribute.
///
/// The `ModuleResolution` attribute has the following format:
///
/// ```
///    ModuleResolution_attribute {
///        u2 attribute_name_index;
///        u4 attribute_length;
///
///        u2 resolution_flags;
///    }
/// ```
///
/// @author Amos Shi
/// @since Java 17
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class ModuleResolution_attribute extends attribute_info {

    /// resolution_flags.
    public final u2 resolution_flags;

    ModuleResolution_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        if (this.attribute_length.value != 2) {
            throw new FileFormatException("The attribute_length of ModuleResolution is not 2, it is %d.".formatted(this.attribute_length.value));
        }

        this.resolution_flags = new u2(posDataInputStream);
        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                u2.LENGTH,
                "resolution_flags: " + BytesTool.getBinaryString(this.resolution_flags.value)
        )));
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_ModuleResolution";
    }
}
