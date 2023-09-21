/*
 * ConstantDoubleInfo.java    4:44 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile.constant;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.core.BytesTool;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.ui.Icons;

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
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.4.5">
 * VM Spec: The CONSTANT_Double_info Structure</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_Double_info extends cp_info {

    public static final int RAW_DATA_SIZE = 8;
    public static final int LENGTH = 9;

    /**
     * In JVM Spec we have two fields: u4 high_bytes and u4 low_bytes.
     */
    public final byte[] rawData;
    public final double doubleValue;

    CONSTANT_Double_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Double.tag);
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
    public String toString(cp_info[] constantPool) {
        return String.valueOf(this.doubleValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        String value = "value = " + this.doubleValue + " Hex Value = " + BytesTool.getByteDataHexView(this.rawData);
        this.addNode(parentNode,
                super.startPos + 1,
                4,
                "high_bytes",
                value,
                "msg_const_double_bytes",
                Icons.Data
        );
        this.addNode(parentNode,
                super.startPos + 5,
                4,
                "low_bytes",
                value,
                "msg_const_double_bytes",
                Icons.Data
        );
    }

    @Override
    public String getMessageKey() {
        return "msg_const_longdouble";
    }
}
