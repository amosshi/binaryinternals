/*
 * ConstantFloatInfo.java    4:41 AM, August 5, 2007
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
 * The class for the {@code CONSTANT_Float_info} structure in constant pool. The
 * {@code CONSTANT_Float_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_Float_info {
 *        u1 tag;
 *        u4 bytes;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since Java 1.0.2
 * @see
 * <a href="https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.4.4">
 * VM Spec: The CONSTANT_Float_info Structure
 * </a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressWarnings("java:S101")
public class CONSTANT_Float_info extends cp_info {

    public static final int RAW_DATA_SIZE = 4;
    public static final int LENGTH = 5;
    public final byte[] rawData;
    public final Float floatValue;

    CONSTANT_Float_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Float.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.rawData = posDataInputStream.getBuf(posDataInputStream.getPos(), RAW_DATA_SIZE);
        this.floatValue = posDataInputStream.readFloat();
    }

    @Override
    public String getDescription() {
        return String.format("%s: Start Position: [%d], length: [%d], value: [%f].",
                this.getName(), this.startPos, this.length, this.floatValue);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_intfloat";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Float.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return String.valueOf(this.floatValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                this.getStartPos() + 1,
                4,
                "bytes",
                this.floatValue + " - " + BytesTool.getByteDataHexView(this.rawData),
                "msg_const_float_bytes",
                Icons.Data
        );
    }
}
