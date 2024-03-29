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
import org.binaryinternals.format.classfile.constant.CONSTANT_Utf8_info;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code ModuleTarget} attribute. The {@code ModuleTarget}
 * attribute has the following format:
 *
 * <pre>
 *    ModuleTarget_attribute {
 *        u2 attribute_name_index;
 *        u4 attribute_length;
 *
 *        // index to a CONSTANT_utf8_info structure
 *        u2 os_arch_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 9
 * @see
 * <a href="https://openjdk.java.net/jeps/261"> JEP 261: Module System</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class ModuleTarget_attribute extends attribute_info {

    /**
     * Index to a {@link CONSTANT_Utf8_info} structure.
     */
    public final u2 os_arch_index;

    ModuleTarget_attribute(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.os_arch_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        int cpIndex = this.os_arch_index.value;
        this.addNode(parentNode,
                super.startPos + 6, u2.LENGTH,
                "os_arch_index", String.format(TEXT_CPINDEX_VALUE, cpIndex, "type name", ((ClassFile)classFile).getCPDescription(cpIndex)),
                "msg_attr_ModuleTarget_os_arch_index", Icons.Name
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_attr_ModuleTarget";
    }
}
