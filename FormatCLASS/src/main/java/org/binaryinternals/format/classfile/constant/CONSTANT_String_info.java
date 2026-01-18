/*
 * ConstantStringInfo.java    4:36 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.3">
 * VM Spec: The CONSTANT_String_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_String_info extends cp_info {

    public static final int LENGTH = 3;
    public final u2 string_index;

    CONSTANT_String_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_String.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.string_index = new u2(posDataInputStream);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_string";
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
    public String toString(cp_info[] constantPool) {
        // The value of the string_index item must be a valid index into the constant_pool table.
        // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure
        // representing the sequence of characters to which the String object is to be initialized.
        return ((CONSTANT_Utf8_info) constantPool[this.string_index.value]).getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "string_index",
                String.format(TEXT_CPINDEX_VALUE, this.string_index.value, "string", ((ClassFile) classFile).getCPDescription(this.string_index.value)),
                "msg_const_string_string_index",
                Icons.Offset
        );
    }
}
