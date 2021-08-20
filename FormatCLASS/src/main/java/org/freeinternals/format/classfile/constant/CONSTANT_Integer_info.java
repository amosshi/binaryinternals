/*
 * ConstantIntegerInfo.java    4:38 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.format.classfile.ClassFile;
import static org.freeinternals.format.classfile.GenerateTreeNodeClassFile.MESSAGES;

/**
 * The class for the {@code CONSTANT_Integer_info} structure in constant pool.
 * The {@code CONSTANT_Integer_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Integer_info {
 *        u1 tag;
 *        u4 bytes;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.4">
 * VM Spec: The CONSTANT_Integer_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_Integer_info extends cp_info {

    public static final int RAW_DATA_SIZE = 4;
    public static final int LENGTH = 5;
    public final byte[] rawData;
    public final int integerValue;

    CONSTANT_Integer_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Integer.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.rawData = posDataInputStream.getBuf(posDataInputStream.getPos(), RAW_DATA_SIZE);
        this.integerValue = posDataInputStream.readInt();
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Integer.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: [%d].",
                this.getName(), super.startPos, super.length, this.integerValue);
    }
    
    @Override
    public String toString(cp_info[] constantPool) {
        return String.valueOf(this.integerValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                4,
                "bytes: " + this.integerValue + " - " + BytesTool.getByteDataHexView(this.rawData),
                UITool.icon4Data(),
                MESSAGES.getString("msg_const_int_bytes")
        )));
    }
}
