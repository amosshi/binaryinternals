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
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.JavaSEVersion;

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
 */
public class ConstantIntegerInfo extends CPInfo {

    public static final int RAW_DATA_SIZE = 4;
    public static final int LENGTH = 5;
    public final byte[] rawData;
    public final int integerValue;

    ConstantIntegerInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Integer.tag, true, ClassFile.Version.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2);
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
    public String toString(CPInfo[] constantPool) {
        return String.valueOf(this.integerValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                4,
                "bytes: " + this.integerValue + " - " + BytesTool.getByteDataHexView(this.rawData)
        )));
    }
}
