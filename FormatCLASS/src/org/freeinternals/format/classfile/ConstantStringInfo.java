/*
 * ConstantStringInfo.java    4:36 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * The class for the {@code CONSTANT_String_info} structure in constant pool.
 * The {@code CONSTANT_String_info} structure has the following format:
 *
 * <pre>
 *    CONSTANT_String_info {
 *        u1 tag;
 *        u2 string_index;
 *    }
 * </pre>
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see <a href="http://www.freeinternals.org/mirror/java.sun.com/vmspec.2nded/ClassFile.doc.html#29297">
 * VM Spec: The CONSTANT_String_info Structure
 * </a>
 */
public class ConstantStringInfo extends CPInfo {

    public static final int LENGTH = 3;
    public final u2 string_index;

    ConstantStringInfo(final PosDataInputStream posDataInputStream)
            throws IOException {
        super(CPInfo.CONSTANT_String);
        super.startPos = posDataInputStream.getPos() - 1;
        super.length = LENGTH;

        this.string_index = new u2(posDataInputStream.readUnsignedShort());
    }

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public String getDescription() {
        return String.format("ConstantStringInfo: Start Position: [%d], length: [%d], value: string_index=[%d].", this.startPos, this.length, this.string_index.value);
    }
}
