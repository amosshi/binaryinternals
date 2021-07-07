/*
 * ConstantStringInfo.java    4:36 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_String_info} structure in constant pool.
 * The {@code CONSTANT_String_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_String_info {
 *        u1 tag;
 *
 *        u2 string_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.3">
 * VM Spec: The CONSTANT_String_info Structure
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class ConstantStringInfo extends CPInfo {

    public static final int LENGTH = 3;
    public final u2 string_index;

    ConstantStringInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_String.tag, true, ClassFile.Version.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.string_index = new u2(posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_String.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: string_index=[%d].", this.getName(), this.startPos, this.length, this.string_index.value);
    }

    @Override
    public String toString(CPInfo[] constantPool) {
        // The value of the string_index item must be a valid index into the constant_pool table.
        // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure
        // representing the sequence of characters to which the String object is to be initialized.
        return ((ConstantUtf8Info) constantPool[this.string_index.value]).getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                2,
                "string_index: " + this.string_index.value + " - " + classFile.getCPDescription(this.string_index.value)
        )));
    }
}
