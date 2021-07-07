/*
 * ConstantDoubleInfo.java    4:44 AM, August 5, 2007
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
 * The class for the {@code CONSTANT_Double_info} structure in constant pool.
 * The {@code CONSTANT_Double_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Double_info {
 *        u1 tag;
 *
 *        u4 high_bytes;
 *        u4 low_bytes;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.4.5">
 * VM Spec: The CONSTANT_Double_info Structure
 * </a>
 */
public class ConstantDoubleInfo extends CPInfo {

    public static final int RAW_DATA_SIZE = 8;
    public static final int LENGTH = 9;

    /**
     * In JVM Spec we have two fields: u4 high_bytes and u4 low_bytes.
     */
    public final byte[] rawData;
    public final double doubleValue;

    ConstantDoubleInfo(final PosDataInputStream posDataInputStream) throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Double.tag, true, ClassFile.Version.FORMAT_45_3, JavaSEVersion.VERSION_1_0_2);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.rawData = posDataInputStream.getBuf(posDataInputStream.getPos(), RAW_DATA_SIZE);
        this.doubleValue = posDataInputStream.readDouble();
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Double.name();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: [%f].",
                this.getName(), this.startPos, this.length, this.doubleValue);
    }

    @Override
    public String toString(CPInfo[] constantPool) {
        return String.valueOf(this.doubleValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, ClassFile classFile) {
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 1,
                4,
                "high_bytes - value: " + this.doubleValue + " - " + BytesTool.getByteDataHexView(this.rawData)
        )));
        parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos + 5,
                4,
                "low_bytes"
        )));
    }
}
