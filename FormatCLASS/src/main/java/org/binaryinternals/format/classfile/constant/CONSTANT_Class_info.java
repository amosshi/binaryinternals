/*
 * ConstantClassInfo.java    4:26 AM, August 5, 2007
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
import org.binaryinternals.format.classfile.SignatureConvertor;
import org.binaryinternals.format.classfile.u2;

/// The class for the `CONSTANT_Class_info` structure in constant pool. The
/// `CONSTANT_Class_info` structure has the following format:
///
/// ```
///    CONSTANT_Class_info {
///        u1 tag;
///        u2 name_index;
///    }
/// ```
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.1">
/// VM Spec: The CONSTANT_Class_info Structure
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings({"java:S101", "java:S116"})
public class CONSTANT_Class_info extends cp_info {

    public static final int LENGTH = 3;
    public final u2 name_index;

    CONSTANT_Class_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Class.tag, posDataInputStream.getPos() - 1, LENGTH);
        this.name_index = new u2(posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Class.name();
    }

    @Override
    public String getDescription() {
        return "%s: Start Position: [%d], length: [%d], value: name_index = [%d].".formatted(
                this.getName(), this.startPos, this.length, this.name_index.value);
    }

    @Override
    public String toString(cp_info[] constantPool) {
        // The value of the name_index item must be a valid index into the constant_pool table.
        // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure
        // representing a valid fully qualified class or interface name encoded in internal form.
        return SignatureConvertor.parseClassSignature(((CONSTANT_Utf8_info) constantPool[this.name_index.value]).getValue());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        final int cpIndex = this.name_index.value;
        this.addNode(parentNode,
                super.startPos + 1,
                2,
                "name_index",
                TEXT_CPINDEX_VALUE.formatted(cpIndex, "class name", ((ClassFile) classFile).getCPDescription(cpIndex)),
                "msg_const_class_name_index",
                Icons.Name);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_class";
    }
}
