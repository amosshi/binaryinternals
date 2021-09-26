/*
 * ConstantNameAndTypeInfo.java    4:46 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_NameAndType_info} structure in constant
 * pool. The {@code CONSTANT_NameAndType_info} structure has the following
 * format:
 *
 * <pre>
 *    CONSTANT_NameAndType_info {
 *        u1 tag;
 * 
 *        u2 name_index;
 *        u2 descriptor_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.4.6">
 * VM Spec: The CONSTANT_NameAndType_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_NameAndType_info extends cp_info {

    public static final int LENGTH = 5;
    public final u2 name_index;
    public final u2 descriptor_index;

    CONSTANT_NameAndType_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_NameAndType.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.name_index = new u2(posDataInputStream);
        this.descriptor_index = new u2(posDataInputStream);
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: name_index=[%d], descriptor_index=[%d].",
                this.getName(), this.startPos, this.length, this.name_index.value, this.descriptor_index.value);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_nameandtype";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_NameAndType.name();
    }
    
    @Override
    public String toString(cp_info[] constantPool) {
        String nameStr = constantPool[this.name_index.value].toString(constantPool);
        String typeStr = constantPool[this.descriptor_index.value].toString(constantPool);

        return String.format("name=%s, type=%s", nameStr, typeStr);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat format) {
        ClassFile classFile = (ClassFile) format;

        int cpIndex = this.name_index.value;
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "name_index",
                String.format(TEXT_CPINDEX_VALUE, cpIndex, "name", classFile.getCPDescription(cpIndex)),
                "msg_const_nameandtype_name_index",
                Icons.Name
        );

        cpIndex = this.descriptor_index.value;
        this.addNode(parentNode,
                super.startPos + 3,
                2,
                "descriptor_index",
                String.format(TEXT_CPINDEX_VALUE, cpIndex, "descriptor", classFile.getCPDescription(cpIndex)),
                "msg_const_nameandtype_descriptor_index",
                Icons.Descriptor
        );
    }
}
