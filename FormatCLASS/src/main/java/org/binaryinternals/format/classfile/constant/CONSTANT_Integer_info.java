/*
 * ConstantIntegerInfo.java    4:38 AM, August 5, 2007
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

/// The class for the `CONSTANT_Integer_info` structure in constant pool.
/// The `CONSTANT_Integer_info` structure has the following format:
///
/// ```
///    CONSTANT_Integer_info {
///        u1 tag;
///        u4 bytes;
///    }
/// ```
///
/// @author Amos Shi
/// @see
/// <a href="https://docs.oracle.com/javase/specs/jvms/se25/html/jvms-4.html#jvms-4.4.4">
/// VM Spec: The CONSTANT_Integer_info Structure
/// </a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressWarnings("java:S101")
public class CONSTANT_Integer_info extends cp_info {

    public static final int RAW_DATA_SIZE = 4;
    public static final int LENGTH = 5;
    public final byte[] rawData;
    public final int integerValue;

    CONSTANT_Integer_info(final PosDataInputStream posDataInputStream) throws IOException {
        super(cp_info.ConstantType.CONSTANT_Integer.tag, posDataInputStream.getPos() - 1, LENGTH);

        this.rawData = posDataInputStream.getBuf(posDataInputStream.getPos(), RAW_DATA_SIZE);
        this.integerValue = posDataInputStream.readInt();
    }

    @Override
    public String getDescription() {
        return "%s: Start Position: [%d], length: [%d], value: [%d].".formatted(
                this.getName(), super.startPos, super.length, this.integerValue);
    }

    @Override
    public String getMessageKey() {
        return "msg_const_intfloat";
    }

    @Override
    public String getName() {
        return ConstantType.CONSTANT_Integer.name();
    }

    @Override
    public String toString(cp_info[] constantPool) {
        return String.valueOf(this.integerValue);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode, FileFormat classFile) {
        this.addNode(parentNode,
                super.startPos + 1,
                4,
                "bytes",
                this.integerValue + " - " + BytesTool.getByteDataHexView(this.rawData),
                "msg_const_int_bytes",
                Icons.Data
        );
    }
}
