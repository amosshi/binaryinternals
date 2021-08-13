/*
 * ConstantClassInfo.java    4:26 AM, August 5, 2007
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
import org.freeinternals.format.classfile.SignatureConvertor;
import org.freeinternals.format.classfile.u2;

/**
 * The class for the {@code CONSTANT_Class_info} structure in constant pool. The
 * {@code CONSTANT_Class_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Class_info {
 *        u1 tag;
 *        u2 name_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.1">
 * VM Spec: The CONSTANT_Class_info Structure
 * </a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S116")
public class ConstantClassInfo extends CPInfo {

    public static final int LENGTH = 3;
    public final u2 name_index;

    ConstantClassInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Class.tag);

        this.startPos = posDataInputStream.getPos() - 1;
        this.length = LENGTH;
        this.name_index = new u2(posDataInputStream);
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Class.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: name_index = [%d].",
                this.getName(), this.startPos, this.length, this.name_index.value);
    }

    @Override
    public String toString(CPInfo[] constantPool) {
        // The value of the name_index item must be a valid index into the constant_pool table.
        // The constant_pool entry at that index must be a CONSTANT_Utf8_info structure
        // representing a valid fully qualified class or interface name encoded in internal form.
        return SignatureConvertor.parseClassSignature(((ConstantUtf8Info) constantPool[this.name_index.value]).getValue());
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                2,
                "name_index: " + this.name_index.value + " - " + classFile.getCPDescription(this.name_index.value)
        )));
    }
}
