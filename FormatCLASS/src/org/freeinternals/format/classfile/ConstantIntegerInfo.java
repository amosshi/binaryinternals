/*
 * ConstantIntegerInfo.java    4:38 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

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
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#21942">
 * VM Spec: The CONSTANT_Integer_info Structure
 * </a>
 */
public class ConstantIntegerInfo extends CPInfo {

    public static final int LENGTH = 5;
    public final int integerValue;

    ConstantIntegerInfo(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(CPInfo.ConstantType.CONSTANT_Integer.tag);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.integerValue = posDataInputStream.readInt();
    }

    @Override
    public String getName() {
        return "Integer";
    }

    @Override
    public String getDescription() {
        return String.format("ConstantIntegerInfo: Start Position: [%d], length: [%d], value: [%d].", this.startPos, this.length, this.integerValue);
    }
}
