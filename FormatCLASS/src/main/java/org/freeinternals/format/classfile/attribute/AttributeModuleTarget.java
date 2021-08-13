/*
 * AttributeInnerClasses.java    5:20 AM, August 5, 2007
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
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class AttributeModuleTarget extends AttributeInfo {

    /**
     * Index to a
     * {@link org.freeinternals.format.classfile.constant.ConstantUtf8Info}
     * structure.
     */
    public final u2 os_arch_index;

    AttributeModuleTarget(final u2 nameIndex, final String type, final PosDataInputStream posDataInputStream) throws IOException, FileFormatException {
        super(nameIndex, type, posDataInputStream);
        this.os_arch_index = new u2(posDataInputStream);

        super.checkSize(posDataInputStream.getPos());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 6,
                u2.LENGTH,
                "os_arch_index: " + this.os_arch_index.value + " - " + classFile.getCPDescription(this.os_arch_index.value)
        )));
    }
}
