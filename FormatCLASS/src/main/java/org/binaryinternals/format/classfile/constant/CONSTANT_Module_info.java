/*
 * ConstantModuleInfo.java    00:14 AM, July 19, 2018
 *
 * Copyright  2018, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;
import org.binaryinternals.format.classfile.ClassFile;
import org.binaryinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_Module_info} structure in constant pool.
 * The {@code CONSTANT_Module_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Module_info {
 *        u1 tag;
 *
 *        u2 name_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 9
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.4.11">
 * VM Spec: The CONSTANT_Module_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_Module_info extends cp_info {

    public static final int LENGTH = 3;

    /**
     * The value of the {@link name_index} item must be a valid index into the
     * {@code constant_pool} table. The constant_pool entry at that index must
     * be a {@link CONSTANT_Utf8_info} structure representing a valid module name.
     */
    public final u2 name_index;

    CONSTANT_Module_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Module.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.name_index = new u2(posDataInputStream);
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: name_index=[%d].",
                this.getName(), super.startPos, this.length, this.name_index.value);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_module";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Module.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return ((CONSTANT_Utf8_info) constantPool[this.name_index.value]).getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int cpIndex = this.name_index.value;
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "name_index",
                String.format(TEXT_CPINDEX_VALUE, cpIndex, "module name", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_const_module_name_index",
                Icons.Name
        );
    }
}
